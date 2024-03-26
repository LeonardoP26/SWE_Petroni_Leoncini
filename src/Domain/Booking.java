package Domain;

import java.util.List;

public class Booking {

    public Booking(int bookingNumber, int showTimeId, List<Integer> seatsId, List<Integer> usersId) {
        this.bookingNumber = bookingNumber;
        this.showTimeId = showTimeId;
        this.seatsId = seatsId;
        this.usersId = usersId;
    }

    private final int bookingNumber;
    private final int showTimeId;
    private final List<Integer> seatsId;
    private final List<Integer> usersId;

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
