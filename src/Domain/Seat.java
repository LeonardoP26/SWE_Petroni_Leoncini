package Domain;

public class Seat {
    private boolean isBooked;
    private int seatNumber;
    private boolean isVip;

    public Seat(int seatNumber, boolean isVip, boolean isBooked) {
        this.seatNumber = seatNumber;
        this.isVip = isVip;
        this.isBooked = isBooked;
    }
}
