package daos;

import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Booking;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BookingDaoInterface  {

    void insert(@NotNull Booking booking) throws SQLException, UnableToOpenDatabaseException;

    int createBookingNumber() throws SQLException, UnableToOpenDatabaseException;

    ResultSet getBooking(int bookingNumber) throws SQLException, UnableToOpenDatabaseException;

    ResultSet getBookingUsers(Booking booking) throws SQLException, UnableToOpenDatabaseException;

    ResultSet getBookingSeats(Booking booking) throws SQLException, UnableToOpenDatabaseException;

    ResultSet getBookingShowTime(Booking booking) throws SQLException, UnableToOpenDatabaseException;
}
