package daos;

import business_logic.CinemaDatabase;
import business_logic.HallFactory;
import business_logic.exceptions.DatabaseFailedException;
import domain.*;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

public class BookingDaoImpl implements BookingDao {

    private static BookingDao instance = null;

    public static BookingDao getInstance(){
        if(instance == null)
            instance = new BookingDaoImpl();
        return instance;
    }

    private BookingDaoImpl() { }


    @Override
    public void insert(@NotNull ShowTime showTime, List<Seat> seats, User user) throws DatabaseFailedException {
        try {
            Connection conn = CinemaDatabase.getConnection();
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try(PreparedStatement ps = conn.prepareStatement(
                    "SELECT MIN(t) AS booking_number FROM (SELECT DISTINCT 1 AS t FROM Bookings WHERE (SELECT MIN(booking_number) FROM Bookings) > 1 UNION SELECT Bookings.booking_number + 1 FROM Bookings WHERE booking_number + 1 NOT IN (SELECT booking_number FROM Bookings))"
            )) {
                try(ResultSet res = ps.executeQuery()) {
                    if(!res.next())
                        throw new DatabaseFailedException("Database insertion failed.");
                    int bookingNumber = res.getInt("booking_number");
                    for (Seat seat : seats) {
                        try (PreparedStatement s = conn.prepareStatement(
                                "INSERT INTO Bookings(showtime_id, seat_id, user_id, booking_number) VALUES (?, ?, ?, ?)"
                        )) {
                            s.setInt(1, showTime.getId());
                            s.setInt(2, seat.getId());
                            s.setInt(3, user.getId());
                            s.setInt(4, bookingNumber);
                            if (s.executeUpdate() == 0)
                                throw new DatabaseFailedException("Database insertion failed.");
                        }
                    }
                    conn.commit();
                }
            } catch (SQLiteException e) {
                conn.rollback();
                if(e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                    throw new DatabaseFailedException("Database insertion failed: this booking already exists.");
                else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                    throw new DatabaseFailedException("Database insertion failed: ensure the showtime id, the seat id, the user id and the booking number are not null.");
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
    public void delete(@NotNull Booking booking) throws DatabaseFailedException {
        try {
            Connection conn = CinemaDatabase.getConnection();
            try (PreparedStatement s = conn.prepareStatement(
                    "DELETE FROM Bookings WHERE booking_number = ?"
            )) {
                s.setInt(1, booking.getBookingNumber());
                if(s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Query result is empty.");
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Booking> get(@NotNull User user) {
        try {
            Connection conn = CinemaDatabase.getConnection();
            try (PreparedStatement s = conn.prepareStatement(
                    "SELECT * FROM ((((Bookings JOIN ShowTimes ON Bookings.showtime_id = ShowTimes.showtime_id) JOIN Seats ON Bookings.seat_id = Seats.seat_id) JOIN Movies ON ShowTimes.movie_id = Movies.movie_id) JOIN Halls ON ShowTimes.hall_id = Halls.hall_id) JOIN Cinemas ON Halls.cinema_id = Cinemas.cinema_id WHERE user_id = ? ORDER BY booking_number"
            )) {
                s.setInt(1, user.getId());
                try(ResultSet res = s.executeQuery()){
                    return getList(res, (bookingList) -> {
                        Booking prevBooking;
                        if(!bookingList.isEmpty())
                            prevBooking = bookingList.getLast();
                        else{
                            prevBooking = new Booking(null, null);
                            prevBooking.setBookingNumber(0);
                        }
                        Booking booking = new Booking(res);
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
                            prevBooking.setSeats(List.of(seat));
                        else
                            prevBooking.setSeats(Stream.concat(prevBooking.getSeats().stream(), Stream.of(seat)).toList());
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
