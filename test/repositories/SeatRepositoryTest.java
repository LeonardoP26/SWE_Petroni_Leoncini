package repositories;

import business_logic.exceptions.InvalidIdException;
import business_logic.repositories.*;
import dao.fake_daos.FakeBookingDao;
import dao.fake_daos.FakeSeatDao;
import dao.fake_daos.FakeShowTimeDao;
import dao.fake_daos.FakeUserDao;
import db.CinemaDatabaseTest;
import domain.Hall;
import domain.Seat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;

import static org.junit.jupiter.api.Assertions.*;

public class SeatRepositoryTest {

    private final BookingRepository bookingRepo = BookingRepositoryImpl.getInstance(new FakeBookingDao());
    private final UserRepository userRepo = UserRepositoryImpl.getInstance(new FakeUserDao(), bookingRepo);
    private final SeatRepository seatRepo = SeatRepositoryImpl.getInstance(new FakeSeatDao(), userRepo);

    @BeforeEach
    public void seatUpEach(){
        CinemaDatabaseTest.setUp();
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
        for(Seat s: CinemaDatabaseTest.getTestSeats()) {
            seatRepo.getEntities().put(s.getId(), new WeakReference<>(s));
        }
    }

    @AfterEach
    public void tearDownEach() {
        CinemaDatabaseTest.tearDown();
        seatRepo.getEntities().clear();
        bookingRepo.getEntities().clear();
        userRepo.getEntities().clear();
    }

    @Test
    public void insert_success(){
        Seat newSeat = new Seat('a', 10);
        Hall h = CinemaDatabaseTest.getTestHall1();
        assertDoesNotThrow(() -> seatRepo.insert(newSeat, h));
        assertTrue(seatRepo.getEntities().containsKey(newSeat.getId()));
        assertSame(seatRepo.getEntities().get(newSeat.getId()).get(), newSeat);
        assertTrue(h.getSeats().contains(newSeat));
    }

    @Test
    public void insertSeat_withHallNotInDatabase_throwsInvalidIdException() {
        assertThrows(InvalidIdException.class, () ->
                seatRepo.insert(new Seat('a', 10), new Hall(2, CinemaDatabaseTest.getTestCinema1()))
        );
    }



}
