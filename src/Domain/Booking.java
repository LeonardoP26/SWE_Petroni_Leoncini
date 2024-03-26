package Domain;

import BusinessLogic.repositories.BookingRepository;
import BusinessLogic.repositories.BookingRepositoryInterface;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Booking {

    public Booking(int bookingNumber, int showTimeId, List<Integer> seatsId, List<Integer> usersId) {
        this.bookingNumber = bookingNumber;
        this.showTimeId = showTimeId;
        this.seatsId = seatsId;
        this.usersId = usersId;
    }

    private int bookingNumber;
    private int showTimeId;
    private List<Integer> seatsId;
    private List<Integer> usersId;

    public int getBookingNumber() {
        return bookingNumber;
    }

    public int getShowTimeId() {
        return showTimeId;
    }

    public List<Integer> getSeatsId() {
        return seatsId;
    }

    public List<Integer> getUsersId() {
        return usersId;
    }




}
