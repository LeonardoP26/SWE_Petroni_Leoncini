package repositories;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.repositories.BookingRepository;
import business_logic.repositories.BookingRepositoryImpl;
import dao.fake_daos.FakeBookingDao;
import daos.BookingDao;
import db.CinemaDatabaseTest;
import domain.Booking;
import domain.DatabaseEntity;
import domain.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookingRepositoryTest {

    private final BookingRepository bookingRepo = BookingRepositoryImpl.getInstance(new FakeBookingDao());

    @BeforeEach
    public void setUpEach(){
        CinemaDatabaseTest.setUp();
        bookingRepo.getEntities().put(CinemaDatabaseTest.getTestBooking1().getId(), new WeakReference<>(CinemaDatabaseTest.getTestBooking1()));
        bookingRepo.getEntities().put(CinemaDatabaseTest.getTestBooking2().getId(), new WeakReference<>(CinemaDatabaseTest.getTestBooking2()));
    }

    @AfterEach
    public void tearDownEach(){
        CinemaDatabaseTest.tearDown();
    }

    @Test
    public void insert_success() {
        Booking b = new Booking(
                CinemaDatabaseTest.getTestShowTime1(),
                new ArrayList<>(CinemaDatabaseTest.getTestSeats().subList(0, 2))
        );
        assertDoesNotThrow(() -> bookingRepo.insert(b, CinemaDatabaseTest.getTestUser1()));
        assertTrue(bookingRepo.getEntities().containsKey(b.getId()));
        Booking cachedBooking = bookingRepo.getEntities().get(b.getId()) != null ? bookingRepo.getEntities().get(b.getId()).get() : null;
        assertEquals(b, cachedBooking);
    }

    @Test
    public void delete_success() {
        Booking b = CinemaDatabaseTest.getTestBooking1();
        User owner = CinemaDatabaseTest.getTestUser1();
        assertDoesNotThrow(() -> bookingRepo.delete(b, owner));
        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, b.getId());
        assertFalse(bookingRepo.getEntities().containsKey(b.getId()));
        assertFalse(owner.getBookings().contains(b));
    }

    @Test
    public void get_success(){
        User user = CinemaDatabaseTest.getTestUser1();
        Booking b = CinemaDatabaseTest.getTestBooking1();
        user.getBookings().remove(b);
        List<Booking> bookings = assertDoesNotThrow(() -> bookingRepo.get(user));
        assertTrue(bookingRepo.getEntities().values().stream().map(Reference::get).toList().containsAll(bookings));
    }

}
