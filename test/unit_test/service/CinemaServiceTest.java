package unit_test.service;

import business_logic.exceptions.InvalidSeatException;
import business_logic.repositories.*;
import business_logic.services.CinemaService;
import business_logic.services.CinemaServiceImpl;
import domain.*;
import org.junit.jupiter.api.Test;
import unit_test.repositories.fake_repositories.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CinemaServiceTest {

    private final BookingRepository bookingRepo = new FakeBookingRepository();
    private final UserRepository userRepo = new FakeUserRepository();
    private final SeatRepository seatRepo = new FakeSeatRepository();
    private final ShowTimeRepository showTimeRepo = new FakeShowTimeRepository();
    private final MovieRepository movieRepo = new FakeMovieRepository();
    private final HallRepository hallRepo = new FakeHallRepository();
    private final CinemaRepository cinemaRepo = new FakeCinemaRepository();
    private final CinemaService service = CinemaServiceImpl.getInstance(
            cinemaRepo,
            hallRepo,
            movieRepo,
            showTimeRepo,
            seatRepo,
            userRepo,
            bookingRepo
    );

    Cinema cinema = new Cinema("ABC");
    Hall hall = new Hall(1, cinema);
    Movie movie = new Movie("ABC", Duration.ofMinutes(90));
    ShowTime sht = new ShowTime(movie, hall, LocalDateTime.now());
    ArrayList<Seat> seats = new ArrayList<>(List.of(new Seat('a', 1)));
    Booking newBooking = new Booking(sht, seats);
    User owner = new User("ABC", "ABC");


    @Test
    public void pay_success(){
        assertDoesNotThrow(() -> owner.setBalance(100));
        assertDoesNotThrow(() -> service.pay(newBooking, null, owner));
    }

    @Test
    public void pay_withTakenSeat_throwsInvalidSeatException(){
        seats.getFirst().setBooked(true);
        assertThrows(InvalidSeatException.class, () -> service.pay(newBooking, null, owner));
    }

}
