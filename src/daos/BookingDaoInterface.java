package daos;

import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Booking;
import Domain.Seat;
import Domain.ShowTime;
import Domain.User;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface BookingDaoInterface  {

    boolean insert(int bookingNumber, @NotNull ShowTime showTime, List<Seat> seats, List<User> users) throws SQLException, UnableToOpenDatabaseException;

    boolean delete(int bookingNumber) throws SQLException, UnableToOpenDatabaseException;

    ResultSet createBookingNumber() throws SQLException, UnableToOpenDatabaseException;

    ResultSet get(@NotNull User user) throws SQLException, UnableToOpenDatabaseException;
}
