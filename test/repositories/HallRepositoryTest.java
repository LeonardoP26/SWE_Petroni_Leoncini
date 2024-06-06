package repositories;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.repositories.*;
import dao.fake_daos.*;
import db.CinemaDatabaseTest;
import domain.Cinema;
import domain.Hall;
import domain.Seat;
import domain.ShowTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HallRepositoryTest {

    private final BookingRepository bookingRepo = BookingRepositoryImpl.getInstance(new FakeBookingDao());
    private final UserRepository userRepo = UserRepositoryImpl.getInstance(new FakeUserDao(), bookingRepo);
    private final ShowTimeRepository showTimeRepo = ShowTimeRepositoryImpl.getInstance(new FakeShowTimeDao(), userRepo);
    private final SeatRepository seatRepo = SeatRepositoryImpl.getInstance(new FakeSeatDao(), userRepo);
    private final HallRepository hallRepo = HallRepositoryImpl.getInstance(new FakeHallDao(), seatRepo, showTimeRepo);

    @BeforeEach
    public void seatUpEach(){
        CinemaDatabaseTest.setUp();
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
    }


    @Test
    public void insertHall_success(){
        Hall h = new Hall(1, CinemaDatabaseTest.getTestCinema1());
        assertDoesNotThrow(() -> hallRepo.insert(h));
        assertTrue(hallRepo.getEntities().containsKey(h.getId()));
    }

    @Test
    public void insertHall_withNullCinema_throwsDatabaseFailedException(){
        Hall h = new Hall(1, null);
        assertThrows(DatabaseFailedException.class, () ->hallRepo.insert(h));
        assertFalse(hallRepo.getEntities().containsKey(h.getId()));
    }

    @Test
    public void insertHall_withCinemaNotInDatabase_throwsInvalidIdException(){
        Hall h = new Hall(1, new Cinema("ABC"));
        assertThrows(InvalidIdException.class, () ->hallRepo.insert(h));
        assertFalse(hallRepo.getEntities().containsKey(h.getId()));
    }

    @Test
    public void updateHall_success(){
        Hall hall = CinemaDatabaseTest.getTestHall1();
        assertDoesNotThrow(() -> hallRepo.update(hall, (h) -> {
            h.setHallNumber(3);
            h.setCinema(CinemaDatabaseTest.getTestCinema2());
        }));
        assertEquals(3, hall.getHallNumber());
        assertEquals(CinemaDatabaseTest.getTestCinema2(), hall.getCinema());
    }

    @Test
    public void updateHall_notInDatabase_throwsInvalidIdException(){
        Hall h = new Hall(1, CinemaDatabaseTest.getTestCinema1());
        assertThrows(InvalidIdException.class, () -> hallRepo.update(h, (hall) -> { }));
    }

    @Test
    public void updateHall_withNullCinema_throwsDatabaseFailedException(){
        Hall hall = CinemaDatabaseTest.getTestHall1();
        assertThrows(DatabaseFailedException.class, () -> hallRepo.update(hall, (h) -> {
            h.setCinema(null);
        }));
    }

    @Test
    public void updateHall_withCinemaNotInDatabase_throwsInvalidIdException(){
        Hall hall = CinemaDatabaseTest.getTestHall1();
        assertThrows(InvalidIdException.class, () -> hallRepo.update(hall, (h) -> {
            h.setCinema(new Cinema("ABC"));
        }));
    }

    @Test
    public void delete_success() {
        Hall hall = CinemaDatabaseTest.getTestHall1();
        int hallId = hall.getId();
        int showTimeId = CinemaDatabaseTest.getTestShowTime1().getId();
        int bookingId = CinemaDatabaseTest.getTestBooking1().getId();
        List<Integer> seatIds = CinemaDatabaseTest.getTestHall1().getSeats().stream().map(Seat::getId).toList();
        assertDoesNotThrow(() -> hallRepo.delete(hall));
        assertFalse(hallRepo.getEntities().containsKey(hallId));
        assertFalse(showTimeRepo.getEntities().containsKey(showTimeId));
        assertFalse(bookingRepo.getEntities().containsKey(bookingId));
        seatIds.forEach(id -> assertFalse(seatRepo.getEntities().containsKey(id)));
        assertTrue(CinemaDatabaseTest.getTestUser1().getBookings().stream().noneMatch(b -> b.getId() == bookingId));
    }

    @Test
    public void deleteHall_notInDatabase_throwsInvalidIdException(){
        assertThrows(InvalidIdException.class, () -> hallRepo.delete(new Hall(3, CinemaDatabaseTest.getTestCinema1())));
    }

    @Test
    public void getHall_fromShowTime_success(){
        Hall hall = assertDoesNotThrow(() -> hallRepo.get(CinemaDatabaseTest.getTestShowTime1()));
        assertEquals(hallRepo.getEntities().get(hall.getId()).get(), hall);
    }

    @Test
    public void getHall_fromShowTimeNotInDatabase_throwsInvalidIdException() {
        assertThrows(InvalidIdException.class, () ->
                hallRepo.get(new ShowTime(
                        CinemaDatabaseTest.getTestMovie1(),
                        CinemaDatabaseTest.getTestHall1(),
                        LocalDateTime.now()
                )));
    }

    @Test
    public void getHall_fromHall_success(){
        Hall h = assertDoesNotThrow(() -> hallRepo.get(CinemaDatabaseTest.getTestHall1()));
        assertEquals(hallRepo.getEntities().get(CinemaDatabaseTest.getTestHall1().getId()).get(), h);
    }

    @Test
    public void getHall_fromHallNotInDatabase_throwsInvalidIdException() {
        assertThrows(InvalidIdException.class, () ->
                hallRepo.get(new Hall(3, CinemaDatabaseTest.getTestCinema1())));
    }

}
