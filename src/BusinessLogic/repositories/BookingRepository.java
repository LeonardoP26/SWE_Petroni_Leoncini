package BusinessLogic.repositories;

import BusinessLogic.HallFactory;
import BusinessLogic.exceptions.DatabaseFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.*;
import daos.BookingDao;
import daos.BookingDaoInterface;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public int insert(@NotNull Booking booking, List<User> users) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException {
        try {
            int bookingNumber = createBookingNumber();
            if (!dao.insert(bookingNumber, booking.getShowTime(), booking.getSeats(), users))
                throw new DatabaseFailedException("Database insertion failed.");
            return bookingNumber;
        } catch (SQLiteException e){
            if(e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database insertion failed: this booking already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database insertion failed: ensure the showtime id, the seat id, the user id and the booking number are not null.");
            else throw e; // TODO throw it as DatabaseInsertionFailedException
        }

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
                ShowTime showTime = new ShowTime(res);
                Cinema cinema = new Cinema(res);
                showTime.setDate(LocalDateTime.parse(res.getString(8), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                showTime.setCinema(cinema);
                showTime.setMovie(new Movie(res));
                showTime.setHall(HallFactory.createHall(res));
                booking.setShowTime(showTime);
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