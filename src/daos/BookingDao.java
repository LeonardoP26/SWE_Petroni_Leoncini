package daos;

import BusinessLogic.CinemaDatabase;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Booking;
import Domain.Seat;
import Domain.User;
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
                s.setInt(4, booking.getBookingNumber());
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
}
