package Domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Booking {

    public Booking(int bookingNumber, Schedule schedule, List<Seat> seats, List<User> users) {
//        if(users.size() < seats.size())
//            throw new NotEnoughSeatsException("You have not chosen enough seats.");
//        for (Seat seat: seats){
//            if(!schedule.getHall().searchSeat(seat))
//                throw new NotAvailableSeatsException("Seats are not available.");
//        }
        this.bookingNumber = bookingNumber;
        this.schedule = schedule;
        this.seats = seats;
        this.users = users;
    }

    private int bookingNumber;
    private Schedule schedule;
    private List<Seat> seats;
    private List<User> users;

    public int getBookingNumber() {
        return bookingNumber;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public List<User> getUsers() {
        return users;
    }
}
