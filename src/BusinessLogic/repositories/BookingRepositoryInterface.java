package BusinessLogic.repositories;

import BusinessLogic.NotAvailableSeatsException;
import BusinessLogic.NotEnoughSeatsException;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface BookingRepositoryInterface {

    Booking book(ShowTime showTime, List<Seat> seats, List<User> users) throws NotAvailableSeatsException, SQLException, NotEnoughSeatsException, UnableToOpenDatabaseException;

    Booking getBooking(int bookingNumber) throws SQLException, UnableToOpenDatabaseException;

    List<User> getBookingUsers(Booking booking) throws SQLException, UnableToOpenDatabaseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    List<Seat> getBookingSeats(Booking booking) throws SQLException, UnableToOpenDatabaseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    ShowTime getBookingShowTime(Booking booking) throws SQLException, UnableToOpenDatabaseException;
}
