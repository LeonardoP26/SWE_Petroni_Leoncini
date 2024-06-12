package unit_test.repositories;

import business_logic.exceptions.InvalidIdException;
import business_logic.repositories.BookingRepository;
import business_logic.repositories.BookingRepositoryImpl;
import business_logic.repositories.UserRepository;
import business_logic.repositories.UserRepositoryImpl;
import db.CinemaDatabaseTest;
import domain.Booking;
import domain.DatabaseEntity;
import domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unit_test.dao.fake_daos.FakeBookingDao;
import unit_test.dao.fake_daos.FakeUserDao;

import java.lang.ref.WeakReference;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest {

    private final BookingRepository bookingRepo = BookingRepositoryImpl.getInstance(new FakeBookingDao());
    private final UserRepository userRepo = UserRepositoryImpl.getInstance(new FakeUserDao(), bookingRepo);

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
    }

    @AfterEach
    public void tearDownEach() {
        CinemaDatabaseTest.tearDown();
        bookingRepo.getEntities().clear();
        userRepo.getEntities().clear();
    }

    @Test
    public void insertUser_success(){
        User newUser = new User("ABC", "ABC");
        assertDoesNotThrow(() -> userRepo.insert(newUser));
        assertTrue(userRepo.getEntities().containsKey(newUser.getId()));
        assertSame(userRepo.getEntities().get(newUser.getId()).get(), newUser);
    }

    @Test
    public void updateUser_success(){
        User testUser = CinemaDatabaseTest.getTestUser1();
        assertDoesNotThrow(() -> userRepo.update(testUser, (u) -> {
            u.setUsername("ABC");
            u.setPassword("ABC");
        }));
        assertEquals("ABC", testUser.getUsername());
        assertEquals("ABC", testUser.getPassword());
    }

    @Test
    public void updateUser_notInDatabase_throwsInvalidIdException(){
        assertThrows(InvalidIdException.class, () -> userRepo.update(new User("ABC", "ABC"), (u) -> { }));
    }

    @Test
    public void deleteUser_success(){
        User testUser = CinemaDatabaseTest.getTestUser1();
        List<Integer> userBookingsIds = testUser.getBookings().stream().map(Booking::getId).toList();
        assertDoesNotThrow(() -> userRepo.delete(testUser));
        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, testUser.getId());
        assertFalse(bookingRepo.getEntities().keySet().containsAll(userBookingsIds));
        assertTrue(testUser.getBookings().stream().allMatch(b -> b.getId() == DatabaseEntity.ENTITY_WITHOUT_ID));
    }

    @Test
    public void deleteUser_notInDatabase_throwsInvalidIdException() {
        assertThrows(InvalidIdException.class, () -> userRepo.delete(new User("ABC", "ABC")));
    }

    @Test
    public void getUser_success(){
        User testUser = CinemaDatabaseTest.getTestUser1();
        User u = assertDoesNotThrow(() -> userRepo.get(testUser.getUsername(), testUser.getPassword()));
        assertSame(testUser, u);
    }

}
