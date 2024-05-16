package domain;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Booking implements DatabaseEntity {

    private int bookingNumber = ENTITY_WITHOUT_ID;
    private ArrayList<Seat> seats = new ArrayList<>();
    private ShowTime showTime;

    public Booking(ResultSet res) throws SQLException {
        this.bookingNumber = res.getInt("booking_number");
    }

    public Booking(ShowTime showTime, ArrayList<Seat> seats) {
        this.showTime = showTime;
        this.seats = seats;
    }

    public Booking(@NotNull Booking booking){
        this.showTime = booking.getShowTime();
        this.seats = booking.getSeats();
    }


    public int getBookingNumber() {
        return bookingNumber;
    }

    @Override
    public String getName() {
        return bookingNumber +  " - " + showTime.getMovie().getName() + " - " + showTime.getName() + " - " + showTime.getCinema().getName();
    }

    public void setBookingNumber(@NotNull ResultSet resultSet) throws SQLException {
        this.bookingNumber = resultSet.getInt("booking_number");
    }

    public ArrayList<Seat> getSeats() {
        return seats;
    }

    public void setSeats(ArrayList<Seat> seats) {
        this.seats = seats;
    }

    public ShowTime getShowTime() {
        return showTime;
    }

    public void setShowTime(ShowTime showTime) {
        this.showTime = showTime;
    }

    @Override
    public int getId() {
        return getBookingNumber();
    }

    @Override
    public void resetId(){
        this.bookingNumber = ENTITY_WITHOUT_ID;
    }

    public void copy(@NotNull Booking booking){
        this.showTime = getShowTime();
        this.seats = getSeats();
    }

}
