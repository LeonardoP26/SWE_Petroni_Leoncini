package daos;

import business_logic.CinemaDatabase;
import business_logic.HallFactory;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    public void insert(@NotNull Booking booking, @NotNull User user, @NotNull User copy) throws DatabaseFailedException {
        List<Seat> seats = booking.getSeats();
        ShowTime showTime = booking.getShowTime();
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try(PreparedStatement s = conn.prepareStatement(
                    "UPDATE Users SET balance = ? WHERE user_id = ?"
            )) {
                s.setLong(1, copy.getBalance());
                s.setInt(2, user.getId());
                if (s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Database insertion failed.");
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
            } catch (SQLiteException | NullPointerException e) {
                conn.rollback();
                if (e instanceof NullPointerException){
                    throw new DatabaseFailedException("Null values are not allowed");
                }
                else if(((SQLiteException) e).getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY)
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
    public void update(@NotNull Booking oldBooking, @NotNull Booking newBooking, @NotNull User user, @NotNull User copy) throws DatabaseFailedException {
        boolean oldAutoCommit;
        try{
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try(PreparedStatement s1 = conn.prepareStatement(
                    "DELETE FROM Bookings WHERE booking_number = ?"
            )){
                s1.setInt(1, oldBooking.getBookingNumber());
                s1.executeUpdate();
                for(Seat seat : newBooking.getSeats()) {
                    try (PreparedStatement s2 = conn.prepareStatement(
                            "INSERT OR ROLLBACK INTO Bookings(showtime_id, seat_id, user_id, booking_number) VALUES (?, ?, ?, ?)"
                    )){
                        s2.setInt(1, newBooking.getShowTime().getId());
                        s2.setInt(2, seat.getId());
                        s2.setInt(3, user.getId());
                        s2.setInt(4, oldBooking.getBookingNumber());
                        s2.executeUpdate();
                        try(PreparedStatement s3 = conn.prepareStatement(
                                "UPDATE Users SET balance = ? WHERE user_id = ?"
                        )){
                            s3.setLong(1, copy.getBalance());
                            s3.setInt(2, user.getId());
                            s3.executeUpdate();
                        }
                    }
                }
                conn.commit();
            } catch (SQLiteException | NullPointerException e){
                conn.rollback();
                if (e instanceof NullPointerException){
                    throw new DatabaseFailedException("Null values are not allowed");
                }
                else if(((SQLiteException) e).getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY)
                    throw new DatabaseFailedException("This booking already exists.");
                else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
            } finally {
                conn.setAutoCommit(oldAutoCommit);
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(@NotNull Booking booking, @NotNull User user) throws DatabaseFailedException {
        boolean oldAutoCommit;
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try(PreparedStatement s = conn.prepareStatement(
                    "UPDATE Users SET balance = ? WHERE user_id = ?"
            )) {
                s.setLong(1, user.getBalance() + (long) booking.getSeats().size() * booking.getShowTime().getHall().getCost());
                s.setInt(2, user.getId());
                if(s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Delete failed");
                try (PreparedStatement s1 = conn.prepareStatement(
                        "DELETE FROM Bookings WHERE booking_number = ?"
                )) {
                    s1.setInt(1, booking.getBookingNumber());
                    if (s1.executeUpdate() == 0)
                        throw new DatabaseFailedException("Query result is empty.");
                    conn.commit();
                }
            } catch (SQLException | DatabaseFailedException e){
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(oldAutoCommit);
                if (conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Booking> get(@NotNull User user) {
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
                            prevBooking = new Booking(null,null, null);
                        if(prevBooking.getBookingNumber() != booking.getBookingNumber()){
                            prevBooking = booking;
                            ShowTime showTime = new ShowTime(res);
                            Cinema cinema = new Cinema(res);
                            showTime.setDate(LocalDateTime.parse(res.getString(8), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                            prevBooking.setCinema(cinema);
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
