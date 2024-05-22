package repositories;

import business_logic.exceptions.InvalidIdException;
import business_logic.repositories.*;
import dao.fake_daos.*;
import db.CinemaDatabaseTest;
import domain.Cinema;
import domain.Seat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class CinemaRepositoryTest {

    private final BookingRepository bookingRepo = BookingRepositoryImpl.getInstance(new FakeBookingDao());
    private final UserRepository userRepo = UserRepositoryImpl.getInstance(new FakeUserDao(), bookingRepo);
    private final ShowTimeRepository showTimeRepo = ShowTimeRepositoryImpl.getInstance(new FakeShowTimeDao(), userRepo);
    private final SeatRepository seatRepo = SeatRepositoryImpl.getInstance(new FakeSeatDao(), userRepo);
    private final HallRepository hallRepo = HallRepositoryImpl.getInstance(new FakeHallDao(), seatRepo, showTimeRepo);
    private final CinemaRepository cinemaRepo = CinemaRepositoryImpl.getInstance(new FakeCinemaDao(), hallRepo);

    @BeforeEach
    public void seatUpEach(){
        CinemaDatabaseTest.setUp();
        cinemaRepo.getEntities().put(
                CinemaDatabaseTest.getTestCinema1().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestCinema1())
        );
        cinemaRepo.getEntities().put(
                CinemaDatabaseTest.getTestCinema2().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestCinema2())
        );
        hallRepo.getEntities().put(
                CinemaDatabaseTest.getTestHall1().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestHall1())
        );
        hallRepo.getEntities().put(
                CinemaDatabaseTest.getTestHall2().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestHall2())
        );
        userRepo.getEntities().put(
                CinemaDatabaseTest.getTestUser1().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestUser1())
        );
        userRepo.getEntities().put(
                CinemaDatabaseTest.getTestUser2().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestUser2())
        );
        bookingRepo.getEntities().put(
                CinemaDatabaseTest.getTestBooking1().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestBooking1())
        );
        bookingRepo.getEntities().put(
                CinemaDatabaseTest.getTestBooking2().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestBooking2())
        );
        showTimeRepo.getEntities().put(
                CinemaDatabaseTest.getTestShowTime1().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestShowTime1())
        );
        showTimeRepo.getEntities().put(
                CinemaDatabaseTest.getTestShowTime2().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestShowTime2())
        );
        for(Seat s: CinemaDatabaseTest.getTestSeats()) {
            seatRepo.getEntities().put(s.getId(), new WeakReference<>(s));
        }
    }

    @AfterEach
    public void tearDownEach(){
        CinemaDatabaseTest.tearDown();
        seatRepo.getEntities().clear();
        showTimeRepo.getEntities().clear();
        bookingRepo.getEntities().clear();
        userRepo.getEntities().clear();
        hallRepo.getEntities().clear();
        cinemaRepo.getEntities().clear();
    }

    @Test
    public void insertCinema_success(){
        Cinema newCinema = new Cinema("ABC");
        assertDoesNotThrow(() -> cinemaRepo.insert(newCinema));
        assertEquals(newCinema, cinemaRepo.getEntities().get(newCinema.getId()).get());
    }

    @Test
    public void updateCinema_success(){
        Cinema cinema = CinemaDatabaseTest.getTestCinema1();
        assertDoesNotThrow(() -> cinemaRepo.update(cinema, (c) -> c.setName("AB")));
        assertEquals(cinema.getName(), Objects.requireNonNull(cinemaRepo.getEntities().get(cinema.getId()).get()).getName());
    }

    @Test
    public void updateCinema_notInDatabase_throwsInvalidIdException(){
        assertThrows(InvalidIdException.class, () -> cinemaRepo.update(new Cinema("A"), (c) -> {
            c.setName("B");
        }));
    }

    @Test
    public void deleteCinema_success(){
        Cinema cinema = CinemaDatabaseTest.getTestCinema1();
        int cinemaId = cinema.getId();
        int hallId = CinemaDatabaseTest.getTestHall1().getId();
        int showTimeId = CinemaDatabaseTest.getTestShowTime1().getId();
        int bookingId = CinemaDatabaseTest.getTestBooking1().getId();
        List<Integer> seatIds = CinemaDatabaseTest.getTestHall1().getSeats().stream().map(Seat::getId).toList();
        assertDoesNotThrow(() -> cinemaRepo.delete(cinema));
        assertFalse(cinemaRepo.getEntities().containsKey(cinemaId));
        assertFalse(hallRepo.getEntities().containsKey(hallId));
        assertFalse(showTimeRepo.getEntities().containsKey(showTimeId));
        assertFalse(bookingRepo.getEntities().containsKey(bookingId));
        seatIds.forEach(id -> assertFalse(seatRepo.getEntities().containsKey(id)));
        assertTrue(CinemaDatabaseTest.getTestUser1().getBookings().stream().noneMatch(b -> b.getId() == bookingId));
    }

    @Test
    public void deleteCinema_notInDatabase_throwsInvalidIdException(){
        assertThrows(InvalidIdException.class, () -> cinemaRepo.delete(new Cinema("A")));
    }

    @Test
    public void getCinema_success(){
        Cinema c = assertDoesNotThrow(() -> cinemaRepo.get(CinemaDatabaseTest.getTestCinema1()));
        assertEquals(cinemaRepo.getEntities().get(c.getId()).get(), c);
    }

    @Test
    public void getCinema_notInDatabase_throwsInvalidIdException(){
        assertThrows(InvalidIdException.class, () -> cinemaRepo.get(new Cinema("A")));
    }

    @Test
    public void getCinemaList_success(){
        List<Cinema> cinemas = assertDoesNotThrow(() -> cinemaRepo.get());
        List<Integer> cinemaIds = cinemas.stream().map(Cinema::getId).toList();
        List<Cinema> cachedCinemas = cinemaRepo.getEntities().entrySet().stream().map((entry) -> {
            if(cinemaIds.contains(entry.getKey()))
                return entry.getValue().get();
            return null;
        }).filter(Objects::nonNull).toList();
        assertEquals(cinemas.size(), cachedCinemas.size());
        assertTrue(cinemas.containsAll(cachedCinemas));
    }

}
