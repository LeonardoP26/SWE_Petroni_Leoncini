package Domain;

import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import BusinessLogic.repositories.BookingRepository;
import BusinessLogic.repositories.BookingRepositoryInterface;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Booking implements DatabaseEntity{

    public Booking(ResultSet res) throws SQLException {
        try{
            this.bookingNumber = res.getInt("Bookings.bookingNumber");
        } catch (SQLException e){
            this.bookingNumber = res.getInt("bookingNumber");
        }
    }

    public Booking(ShowTime showTime, List<Seat> seats) {
        this.showTime = showTime;
        this.seats = seats;
    }

    private int bookingNumber = ENTITY_WITHOUT_ID;
    private List<Seat> seats;
    private ShowTime showTime;


    public int getBookingNumber() {
        return bookingNumber;
    }

    @Override
    public String getName() {
        return String.valueOf(bookingNumber);
    }

    public void setBookingNumber(int bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public ShowTime getShowTime() {
        return showTime;
    }

    public void setShowTime(ShowTime showTime) {
        this.showTime = showTime;
    }
}
