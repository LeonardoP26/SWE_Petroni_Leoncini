package Domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Booking implements DatabaseEntity{

    public Booking(ResultSet res) throws SQLException {
        this.bookingNumber = res.getInt("booking_number");
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
        return bookingNumber +  " - " + showTime.getMovie().getName() + " - " + showTime.getName() + " - " + showTime.getCinema().getName();
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
