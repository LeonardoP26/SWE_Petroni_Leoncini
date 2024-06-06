package repositories;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.repositories.*;
import dao.fake_daos.FakeBookingDao;
import dao.fake_daos.FakeSeatDao;
import dao.fake_daos.FakeUserDao;
import db.CinemaDatabaseTest;
import domain.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

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

    @Test
    public void updateSeat_success(){
        Hall testSeatHall = CinemaDatabaseTest.getTestHall1();
        Seat testSeat = testSeatHall.getSeats().getFirst();
        assertDoesNotThrow(() -> seatRepo.update(testSeat, testSeatHall, (s) -> {
            s.setNumber(10);
            s.setRow('b');
        }));
        assertEquals(10, testSeat.getNumber());
        assertEquals('b', testSeat.getRow());
    }

    @Test
    public void updateSeat_withInvalidIds_throwsInvalidIdException(){
        Seat testSeat = new Seat('a', 1);
        assertThrows(InvalidIdException.class, () -> seatRepo.update(testSeat, CinemaDatabaseTest.getTestHall1(), (s) -> {

        }));
        Seat testSeat1 = CinemaDatabaseTest.getTestSeats().getFirst();
        assertThrows(InvalidIdException.class, () ->
                seatRepo.update(testSeat1, new Hall(1, CinemaDatabaseTest.getTestCinema1()), (s) -> {})
        );
    }

    @Test
    public void updateSeat_withWrongHall_throwsDatabaseFailedException(){
        Seat testSeat = CinemaDatabaseTest.getTestHall1().getSeats().getFirst();
        assertThrows(DatabaseFailedException.class, () -> seatRepo.update(testSeat, CinemaDatabaseTest.getTestHall2(), (s) -> { }));
    }

    @Test
    public void deleteSeat_success(){
        User testUser = CinemaDatabaseTest.getTestUser1();
        Booking testBooking = testUser.getBookings().getFirst();
        long newBalance = testUser.getBalance() + testBooking.getShowTime().getHall().getCost();
        Seat testSeat = testBooking.getSeats().getFirst();
        Hall testSeatHall = testBooking.getShowTime().getHall();
        assertDoesNotThrow(() -> seatRepo.delete(testSeat, testSeatHall));
        assertFalse(testBooking.getSeats().contains(testSeat));
        assertFalse(testSeatHall.getSeats().contains(testSeat));
        assertEquals(newBalance, testUser.getBalance());
    }

    @Test
    public void deleteSeat_notInDatabase_throwsInvalidIdException() {
        assertThrows(InvalidIdException.class, () ->
                seatRepo.delete(new Seat('a', 1), CinemaDatabaseTest.getTestHall1())
        );
    }

    @Test
    public void deleteSeat_withWrongHall_throwsDatabaseFailedException() {
        Seat testSeat = CinemaDatabaseTest.getTestHall1().getSeats().getFirst();
        assertThrows(DatabaseFailedException.class, () -> seatRepo.delete(testSeat, CinemaDatabaseTest.getTestHall2()));
    }

    @Test
    public void getSeat_withShowTime_success(){
        List<Seat> seats = assertDoesNotThrow(() -> seatRepo.get(CinemaDatabaseTest.getTestShowTime1()));
        List<Integer> seatIds = seats.stream().map(Seat::getId).toList();
        List<Seat> cachedSeat = seatRepo.getEntities().entrySet().stream().map((entry) -> {
            if(seatIds.contains(entry.getKey()))
                return entry.getValue().get();
            return null;
        }).filter(Objects::nonNull).toList();
        assertEquals(seats.size(), cachedSeat.size());
        assertTrue(seats.containsAll(cachedSeat));
    }

    @Test
    public void getSeat_withShowTimeNotInDatabase_throwsInvalidIdException(){
        assertThrows(InvalidIdException.class, () -> seatRepo.get(new ShowTime(null, null, null)));
    }

    @Test
    public void getSeat_withId_success(){
        Seat testSeat = CinemaDatabaseTest.getTestSeats().getFirst();
        Seat s = assertDoesNotThrow(() -> seatRepo.get(testSeat));
        assertSame(s, testSeat);
    }

    @Test
    public void getSeat_withSeatNotInDatabase_throwsInvalidIdException(){
        assertThrows(InvalidIdException.class, () -> seatRepo.get(new Seat('a', 1)));
    }

}
