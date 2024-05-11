package daos;

import business_logic.CinemaDatabase;
import business_logic.HallFactory;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.*;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookingDaoImpl implements BookingDao {

    private static final HashMap<String, WeakReference<BookingDao>> instances = new HashMap<>();
    private final String dbUrl;

    public static @NotNull BookingDao getInstance(){
        return getInstance(CinemaDatabase.DB_URL);
    }

    public static @NotNull BookingDao getInstance(@NotNull String dbUrl){
        BookingDao inst = instances.get(dbUrl) != null ? instances.get(dbUrl).get() : null;
        if(inst != null)
            return inst;
        inst = new BookingDaoImpl(dbUrl);
        instances.put(dbUrl, new WeakReference<>(inst));
        return inst;
    }

    private BookingDaoImpl(String dbUrl){
        this.dbUrl = dbUrl;
    }

    @Override
    public void insert(@NotNull Booking booking, @NotNull User user, @NotNull User copy) throws DatabaseFailedException, InvalidIdException {
        List<Seat> seats = booking.getSeats();
        ShowTime showTime = booking.getShowTime();
        if(seats == null)
            throw new DatabaseFailedException("Seats list is null.");
        if(showTime == null)
            throw new DatabaseFailedException("Showtime list is null.");
        if (seats.stream().anyMatch(s -> s.getId() == DatabaseEntity.ENTITY_WITHOUT_ID))
            throw new InvalidIdException("These seats are not in the database");
        if (showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database");
        if (user.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This user is not in the database");
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try(PreparedStatement s = conn.prepareStatement(
                    "UPDATE Users SET balance = ? WHERE user_id = ?"
            )) {
                s.setLong(1, copy.getBalance());
                s.setInt(2, user.getId());
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT MIN(t) AS booking_number FROM (SELECT DISTINCT 1 AS t FROM Bookings WHERE (SELECT MIN(booking_number) FROM Bookings) > 1 UNION SELECT Bookings.booking_number + 1 FROM Bookings WHERE booking_number + 1 NOT IN (SELECT booking_number FROM Bookings))"
                )) {
                    try (ResultSet res = ps.executeQuery()) {
                        if (!res.next())
                            throw new DatabaseFailedException("Database insertion failed.");
                        int bookingNumber = res.getInt("booking_number");
                        StringBuilder sql = new StringBuilder("INSERT INTO Bookings(showtime_id, seat_id, user_id, booking_number) VALUES ");
                        for (Seat seat : seats) {
                            sql.append("(%d, %d, %d, %d), ".formatted(showTime.getId(), seat.getId(), user.getId(), bookingNumber));
                        }
                        sql.replace(sql.length() - 2, sql.length(), "");
                        try (PreparedStatement s1 = conn.prepareStatement(sql.toString())) {
                            if (s1.executeUpdate() == 0)
                                throw new DatabaseFailedException("Database insertion failed.");
                        }
                        booking.setBookingNumber(res);
                    }
                }
                conn.commit();
            } catch (SQLiteException e) {
                conn.rollback();
                if(e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY)
                    throw new DatabaseFailedException("This booking already exists.");
                else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
            } finally {
                conn.setAutoCommit(oldAutoCommit);
                if (conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLException ex){
            throw  new RuntimeException(ex);
        }
    }

    // TODO Add update, delete and get methods to respect CRUD principle


    @Override
    public void update(@NotNull Booking oldBooking, @NotNull Booking newBooking, @NotNull User user, @NotNull User copy) throws DatabaseFailedException, InvalidIdException {
        delete(oldBooking, user, false);
        insert(newBooking, user, copy);
    }

    @Override
    public void delete(@NotNull Booking booking, @NotNull User user, boolean commit) throws DatabaseFailedException, InvalidIdException {
        boolean oldAutoCommit;
        try {
            if (booking.getBookingNumber() == DatabaseEntity.ENTITY_WITHOUT_ID)
                throw new InvalidIdException("This booking is already not in the database");
            if(user.getBookings() == null || user.getBookings().isEmpty() || !user.getBookings().contains(booking))
                throw new DatabaseFailedException("This booking does not belong to this user.");
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try(PreparedStatement s = conn.prepareStatement(
                    "UPDATE Users SET balance = ? WHERE user_id = ?"
            )) {
                s.setLong(1, user.getBalance() + (long) booking.getSeats().size() * booking.getShowTime().getHall().getCost());
                s.executeUpdate();
                try (PreparedStatement s1 = conn.prepareStatement(
                        "DELETE FROM Bookings WHERE booking_number = ?"
                )) {
                    s1.setInt(1, booking.getBookingNumber());
                    if (s1.executeUpdate() == 0)
                        throw new DatabaseFailedException("Query result is empty.");
                    if(commit)
                        conn.commit();
                }
            } catch (SQLException | DatabaseFailedException | NullPointerException e){
                if(e instanceof NullPointerException){
                    throw new DatabaseFailedException("Showtime, hall and seats can not be null");
                }
                conn.rollback();
                throw e;
            } finally {
                if(commit)
                    conn.setAutoCommit(oldAutoCommit);
                if (conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Booking> get(@NotNull User user) throws InvalidIdException {
        if(user.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This user is not in the database.");
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try (PreparedStatement s = conn.prepareStatement(
                    "SELECT * FROM ((((Bookings JOIN ShowTimes ON Bookings.showtime_id = ShowTimes.showtime_id) JOIN Seats ON Bookings.seat_id = Seats.seat_id) JOIN Movies ON ShowTimes.movie_id = Movies.movie_id) JOIN Halls ON ShowTimes.hall_id = Halls.hall_id) JOIN Cinemas ON Halls.cinema_id = Cinemas.cinema_id WHERE user_id = ? ORDER BY booking_number"
            )) {
                s.setInt(1, user.getId());
                try(ResultSet res = s.executeQuery()){
                    return getList(res, (bookingList) -> {
                        Booking booking = new Booking(res);
                        Booking prevBooking;
                        if(!bookingList.isEmpty())
                            prevBooking = bookingList.getLast();
                        else
                            prevBooking = new Booking(null, null);
                        if(prevBooking.getBookingNumber() != booking.getBookingNumber()){
                            prevBooking = booking;
                            ShowTime showTime = new ShowTime(res);
                            Cinema cinema = new Cinema(res);
                            showTime.setDate(LocalDateTime.parse(res.getString(8), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                            showTime.setCinema(cinema);
                            showTime.setMovie(new Movie(res));
                            showTime.setHall(HallFactory.createHall(res));
                            prevBooking.setShowTime(showTime);
                        } else
                            bookingList.removeLast();
                        Seat seat = new Seat(res);
                        if(prevBooking.getSeats() == null)
                            prevBooking.setSeats(new ArrayList<>(List.of(seat)));
                        else
                            prevBooking.getSeats().add(seat);
                        return prevBooking;
                    });
                }
            } finally {
                if (conn.getAutoCommit())
                    conn.close();
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }


}
