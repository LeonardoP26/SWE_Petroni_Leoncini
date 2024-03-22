package daos;

import BusinessLogic.CinemaDatabase;
import Domain.Booking;
import Domain.Seat;
import Domain.User;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BookingDao implements BookingDaoInterface{

    @Override
    public void insert(@NotNull Booking booking) throws SQLException{
        Connection con = CinemaDatabase.getConnection();
        for(User user : booking.getUsers()) {
            for(Seat seat: booking.getSeats()) {
                PreparedStatement s = con.prepareStatement(
                        "INSERT OR IGNORE INTO Bookings(scheduleId, seatId, userId, bookingNumber) VALUES (?, ?, ?, ?)"
                );
                s.setInt(1, booking.getSchedule().getId());
                s.setInt(2, seat.getId());
                s.setInt(3, user.getId());
                s.setInt(4, booking.getBookingNumber());
                s.executeUpdate();
            }
        }
    }

}
