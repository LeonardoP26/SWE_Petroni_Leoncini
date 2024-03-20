package Domain;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Booking {

    public Booking(Movie movie, LocalDateTime date, Hall hall, ArrayList<Seat> seats, ArrayList<User> users){

    }

    public Booking(Schedule schedule, ArrayList<Seat> seats, ArrayList<User> users) throws NotEnoughSeatsException, InvalidSeatException, NotAvailableSeatsException {
        if(users.size() < seats.size())
            throw new NotEnoughSeatsException("You have not chosen enough seats.");
        for (Seat seat: seats){
            if(!schedule.getHall().searchSeat(seat))
                throw new NotAvailableSeatsException("Seats are not available.");
        }
        this.date = schedule.getDate();
        this.hall = schedule.getHall();
        this.movie = schedule.getMovie();
        this.seats = seats;
        this.users = users;
    }

    private Movie movie;
    private LocalDateTime date;
    private Hall hall;
    private ArrayList<Seat> seats;
    private ArrayList<User> users;



}
