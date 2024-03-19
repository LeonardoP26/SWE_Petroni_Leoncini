package BusinessLogic;

import Domain.Schedule;
import Domain.Seat;
import Domain.User;

public class BookingManager {

    public void bookSeat(Schedule schedule, Seat seat, User user) throws NoAvailableSeatsException {
        if(seat.isBooked()) {
            throw new NoAvailableSeatsException();
        }

        seat.setBooked(true);
    }


    // Altre funzioni relative alla gestione delle prenotazioni...
}