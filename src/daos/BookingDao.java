package daos;

import BusinessLogic.CinemaDatabase;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Booking;
import Domain.Seat;
import Domain.ShowTime;
import Domain.User;
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
    public boolean insert(int bookingNumber, @NotNull ShowTime showTime, List<Seat> seats, List<User> users) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        boolean oldAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            for (User user : users) {
                for (Seat seat : seats) {
                    try (PreparedStatement s = conn.prepareStatement(
                            "INSERT INTO Bookings(showTimeId, seatId, userId, bookingNumber) VALUES (?, ?, ?, ?)"
                    )) {
                        s.setInt(1, showTime.getId());
                        s.setInt(2, seat.getId());
                        s.setInt(3, user.getId());
                        s.setInt(4, bookingNumber);
                        s.executeUpdate();
                    }
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
    public boolean delete(int bookingNumber) throws SQLException, UnableToOpenDatabaseException {
        try(PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "DELETE FROM Bookings WHERE bookingNumber = ?"
        )){
            s.setInt(1, bookingNumber);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public ResultSet createBookingNumber() throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        Statement s = conn.createStatement();
        return s.executeQuery(
                "SELECT DISTINCT bookingNumber FROM Bookings WHERE bookingNumber = (SELECT DISTINCT max(bookingNumber) FROM Bookings)"
        );
    }

    @Override
    public ResultSet get(@NotNull User user) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM (Bookings JOIN ShowTimes ON Bookings.showTimeId = ShowTimes.id) JOIN Seats ON seatId = Seats.id WHERE userId = ?"
        );
        s.setInt(1, user.getId());
        return s.executeQuery();
    }


}
