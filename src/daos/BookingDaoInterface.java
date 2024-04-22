package daos;

import domain.Seat;
import domain.ShowTime;
import domain.User;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface BookingDaoInterface  {

    boolean insert(int bookingNumber, @NotNull ShowTime showTime, List<Seat> seats, User user) throws SQLException;

    boolean delete(int bookingNumber) throws SQLException;

    ResultSet createBookingNumber() throws SQLException;

    ResultSet get(@NotNull User user) throws SQLException;
}
