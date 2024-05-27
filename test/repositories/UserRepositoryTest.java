package repositories;

import business_logic.repositories.*;
import dao.fake_daos.FakeBookingDao;
import dao.fake_daos.FakeSeatDao;
import dao.fake_daos.FakeUserDao;
import db.CinemaDatabaseTest;
import domain.Seat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.lang.ref.WeakReference;

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

}
