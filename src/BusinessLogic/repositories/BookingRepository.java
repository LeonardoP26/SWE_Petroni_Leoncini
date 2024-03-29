package BusinessLogic.repositories;

import BusinessLogic.NotAvailableSeatsException;
import BusinessLogic.NotEnoughSeatsException;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.*;
import daos.BookingDao;
import daos.BookingDaoInterface;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BookingRepository extends Repository implements BookingRepositoryInterface {
    private final BookingDaoInterface dao = BookingDao.getInstance();
    private static BookingRepositoryInterface instance = null;

    private BookingRepository() { }


    public static BookingRepositoryInterface getInstance(){
        if(instance == null)
            instance = new BookingRepository();
        return instance;
    }

    @Override
    public Booking book(ShowTime showTime, List<Seat> seats, List<User> users) throws NotAvailableSeatsException, SQLException, UnableToOpenDatabaseException, NotEnoughSeatsException {
        if (seats.size() < users.size())
            throw new NotEnoughSeatsException("You have not chosen enough seats.");
        if (seats.stream().anyMatch(Seat::isBooked))
            throw new NotAvailableSeatsException("Some of these seats are already booked.");
        for(Seat s : seats){
            s.setBooked(true);
        }
        Booking booking = new Booking(dao.createBookingNumber(), showTime.getId(), seats.stream().map(Seat::getId).toList(), users.stream().map(User::getId).toList());
        dao.insert(booking);
        return booking;
    }

    @Override
    public Booking getBooking(int bookingNumber) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.getBooking(bookingNumber)){
            if(isQueryResultEmpty(res))
                return null;
            return new Booking(res);
        }
    }

    @Override
    public List<User> getBookingUsers(Booking booking) throws SQLException, UnableToOpenDatabaseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        try(ResultSet res = dao.getBookingUsers(booking)){
            return getList(res, User.class);
        }
    }

    @Override
    public List<Seat> getBookingSeats(Booking booking) throws SQLException, UnableToOpenDatabaseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        try(ResultSet res = dao.getBookingSeats(booking)){
            return getList(res, Seat.class);
        }
    }

    @Override
    public ShowTime getBookingShowTime(Booking booking) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.getBookingShowTime(booking)){
            if(isQueryResultEmpty(res))
                return null;
            return new ShowTime(res);
        }
    }


}