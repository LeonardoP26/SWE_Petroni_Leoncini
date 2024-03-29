package daos;

import BusinessLogic.CinemaDatabase;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Booking;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class BookingDao implements BookingDaoInterface{

    private static BookingDaoInterface instance = null;

    public static BookingDaoInterface getInstance(){
        if(instance == null)
            instance = new BookingDao();
        return instance;
    }

    private BookingDao() { }


    @Override
    public void insert(@NotNull Booking booking) throws SQLException, UnableToOpenDatabaseException {
        Connection con = CinemaDatabase.getConnection();
        for(int userId : booking.getUsersId()) {
            for(int seatId: booking.getSeatsId()) {
                PreparedStatement s = con.prepareStatement(
                        "INSERT OR IGNORE INTO Bookings(showTimeId, seatId, userId, bookingNumber) VALUES (?, ?, ?, ?)"
                );
                s.setInt(1, booking.getShowTimeId());
                s.setInt(2, seatId);
                s.setInt(3, userId);
                s.setInt(4, booking.getId());
                s.executeUpdate();
            }
        }
    }

    @Override
    public int createBookingNumber() throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        Statement s = conn.createStatement();
        ResultSet res = s.executeQuery(
                "SELECT DISTINCT bookingNumber FROM Bookings WHERE bookingNumber = (SELECT DISTINCT max(bookingNumber) FROM Bookings)"
        );
        return res.getInt("bookingNumber") + 1;
    }

    @Override
    public ResultSet getBooking(int bookingNumber) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM Bookings WHERE bookingNumber = ?"
        );
        s.setInt(1, bookingNumber);
        return s.executeQuery();
    }

    @Override
    public ResultSet getBookingUsers(Booking booking) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT id, username, balance FROM Bookings JOIN Users on Bookings.userId = Users.id WHERE bookingNumber = ?"
        );
        s.setInt(1, booking.getId());
        return s.executeQuery();
    }

    @Override
    public ResultSet getBookingSeats(Booking booking) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT id, row, number, isBooked, hallId FROM Bookings JOIN Seats on id = seatId WHERE bookingNumber = ?"
        );
        s.setInt(1, booking.getId());
        return s.executeQuery();
    }

    @Override
    public ResultSet getBookingShowTime(Booking booking) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT id, movieId, hallId, date FROM Bookings JOIN ShowTimes ON showTimeId = id WHERE bookingNumber = ?"
        );
        s.setInt(1, booking.getId());
        return s.executeQuery();
    }

}
