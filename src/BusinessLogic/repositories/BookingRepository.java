package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseInsertionFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.*;
import daos.BookingDao;
import daos.BookingDaoInterface;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    public int insert(@NotNull Booking booking, List<User> users) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException {
        int bookingNumber = createBookingNumber();
        if (!dao.insert(bookingNumber, booking.getShowTime(), booking.getSeats(), users))
            throw new DatabaseInsertionFailedException("Database insertion failed.");
        return bookingNumber;

    }

    @Override
    public boolean delete(@NotNull Booking booking) throws SQLException, UnableToOpenDatabaseException {
        return dao.delete(booking.getBookingNumber());
    }

    private int createBookingNumber() throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.createBookingNumber()){
            if(res.next())
                return res.getInt(1);
            return 1;
        }
    }

    @Override
    public List<Booking> get(@NotNull User user) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.get(user)){
            return getList(res, () -> {
                Booking booking = new Booking(res);
                booking.setShowTime(new ShowTime(res));
                List<Seat> seats = new ArrayList<>();
                do{
                    seats.add(new Seat(res));
                }
                while(res.getInt(4) == booking.getBookingNumber() && res.next());
                booking.setSeats(seats);
                return booking;
            });
        }
    }


}