package BusinessLogic.repositories;

import Domain.NotAvailableSeatsException;
import Domain.Schedule;
import Domain.Seat;
import Domain.User;

public class BookingManager {

    public void bookSeat(Schedule schedule, Seat seat, User user) throws NotAvailableSeatsException {
        if(seat.isBooked()) {
            throw new NotAvailableSeatsException("");
        }

        seat.setBooked(true);
    }


    // Altre funzioni relative alla gestione delle prenotazioni...


}