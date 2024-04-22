package daos;

import business_logic.CinemaDatabase;
import domain.Seat;
import domain.ShowTime;
import domain.User;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.List;

public class BookingDao implements BookingDaoInterface{

    private static BookingDaoInterface instance = null;

    public static BookingDaoInterface getInstance(){
        if(instance == null)
            instance = new BookingDao();
        return instance;
    }

    private BookingDao() { }


    @Override
    public boolean insert(int bookingNumber, @NotNull ShowTime showTime, List<Seat> seats, User user) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        boolean oldAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            for (Seat seat : seats) {
                try (PreparedStatement s = conn.prepareStatement(
                        "INSERT INTO Bookings(showtime_id, seat_id, user_id, booking_number) VALUES (?, ?, ?, ?)"
                )) {
                    s.setInt(1, showTime.getId());
                    s.setInt(2, seat.getId());
                    s.setInt(3, user.getId());
                    s.setInt(4, bookingNumber);
                    s.executeUpdate();
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e){
            conn.rollback();
            return false;
        } finally {
            conn.setAutoCommit(oldAutoCommit);
        }
    }

    // TODO Add update, delete and get methods to respect CRUD principle

    @Override
    public boolean delete(int bookingNumber) throws SQLException {
        try(PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "DELETE FROM Bookings WHERE booking_number = ?"
        )){
            s.setInt(1, bookingNumber);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public ResultSet createBookingNumber() throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        Statement s = conn.createStatement();
        return s.executeQuery(
                "SELECT MIN(t) AS booking_number FROM (SELECT DISTINCT 1 AS t FROM Bookings WHERE (SELECT MIN(booking_number) FROM Bookings) > 1 UNION SELECT Bookings.booking_number + 1 FROM Bookings WHERE booking_number + 1 NOT IN (SELECT booking_number FROM Bookings))"
        );
    }

    @Override
    public ResultSet get(@NotNull User user) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM ((((Bookings JOIN ShowTimes ON Bookings.showtime_id = ShowTimes.showtime_id) JOIN Seats ON Bookings.seat_id = Seats.seat_id) JOIN Movies ON ShowTimes.movie_id = Movies.movie_id) JOIN Halls ON ShowTimes.hall_id = Halls.hall_id) JOIN Cinemas ON Halls.cinema_id = Cinemas.cinema_id WHERE user_id = ? ORDER BY booking_number"
        );
        s.setInt(1, user.getId());
        return s.executeQuery();
    }


}
