package BusinessLogic.repositories;

import BusinessLogic.HallFactory;
import BusinessLogic.exceptions.DatabaseFailedException;
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
import java.util.List;
import java.util.stream.Stream;

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
    public int insert(@NotNull Booking booking, List<User> users) throws DatabaseFailedException {
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
            else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
        } catch (SQLException ex){
            throw  new RuntimeException(ex);
        }

    }

    @Override
    public boolean delete(@NotNull Booking booking) {
        try{
            return dao.delete(booking.getBookingNumber());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int createBookingNumber() throws SQLException {
        try(ResultSet res = dao.createBookingNumber()){
            if(res.next())
                return res.getInt(1);
            return 1;
        }
    }

    @Override
    public List<Booking> get(@NotNull User user) {
        try(ResultSet res = dao.get(user)){
            return getList(res, (bookingList) -> {
                Booking prevBooking;
                if(!bookingList.isEmpty())
                    prevBooking = bookingList.getLast();
                else{
                    prevBooking = new Booking(null, null);
                    prevBooking.setBookingNumber(0);
                }
                Booking booking = new Booking(res);
                if(prevBooking.getBookingNumber() != booking.getBookingNumber()){
                    prevBooking = booking;
                    ShowTime showTime = new ShowTime(res);
                    Cinema cinema = new Cinema(res);
                    showTime.setDate(LocalDateTime.parse(res.getString(8), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    showTime.setCinema(cinema);
                    showTime.setMovie(new Movie(res));
                    showTime.setHall(HallFactory.createHall(res));
                    prevBooking.setShowTime(showTime);
                } else
                    bookingList.removeLast();
                Seat seat = new Seat(res);
                if(prevBooking.getSeats() == null)
                    prevBooking.setSeats(List.of(seat));
                else
                    prevBooking.setSeats(Stream.concat(prevBooking.getSeats().stream(), Stream.of(seat)).toList());
                return prevBooking;
            });
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


}