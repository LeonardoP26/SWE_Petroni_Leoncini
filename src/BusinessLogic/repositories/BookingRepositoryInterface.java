package BusinessLogic.repositories;

import BusinessLogic.NotAvailableSeatsException;
import BusinessLogic.NotEnoughSeatsException;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.*;

import java.sql.SQLException;
import java.util.List;

public interface BookingRepositoryInterface {

    Booking book(ShowTime showTime, List<Seat> seats, List<User> users) throws NotAvailableSeatsException, SQLException, NotEnoughSeatsException, UnableToOpenDatabaseException;

}
