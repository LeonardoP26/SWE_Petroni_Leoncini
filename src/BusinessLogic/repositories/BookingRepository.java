package BusinessLogic.repositories;

import BusinessLogic.NotAvailableSeatsException;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.*;
import daos.BookingDao;
import daos.BookingDaoInterface;

import java.sql.SQLException;
import java.util.List;

public class BookingRepository implements BookingRepositoryInterface {
    private final BookingDaoInterface dao = BookingDao.getInstance();
    private static BookingRepositoryInterface instance = null;

    private BookingRepository() { }


    public static BookingRepositoryInterface getInstance(){
        if(instance == null)
            instance = new BookingRepository();
        return instance;
    }

    @Override
    public Booking book(ShowTime showTime, List<Seat> seats, List<User> users) throws NotAvailableSeatsException, SQLException, UnableToOpenDatabaseException {
        for (Seat s: seats) {
            if (s.isBooked())
                throw new NotAvailableSeatsException(s.getRow() + String.valueOf(s.getNumber()) + " is already booked.");
            s.setBooked(true);
        }
        Booking booking = new Booking(dao.createBookingNumber(), showTime.getId(), seats.stream().map(Seat::getId).toList(), users.stream().map(User::getId).toList());
        dao.insert(booking);
        return booking;
    }


}