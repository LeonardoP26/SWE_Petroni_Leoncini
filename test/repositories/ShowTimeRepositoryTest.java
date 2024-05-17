package repositories;

import business_logic.repositories.*;
import dao.fake_daos.FakeBookingDao;
import dao.fake_daos.FakeSeatDao;
import dao.fake_daos.FakeShowTimeDao;
import dao.fake_daos.FakeUserDao;
import db.CinemaDatabaseTest;
import domain.Cinema;
import domain.ShowTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ShowTimeRepositoryTest {

    private final BookingRepository bookingRepo = BookingRepositoryImpl.getInstance(new FakeBookingDao());
    private final UserRepository userRepo = UserRepositoryImpl.getInstance(new FakeUserDao(), bookingRepo);
    private final ShowTimeRepository showTimeRepo = ShowTimeRepositoryImpl.getInstance(new FakeShowTimeDao(), userRepo);

    @BeforeEach
    public void setUpEach(){
        CinemaDatabaseTest.setUp();
        bookingRepo.getEntities().put(
                CinemaDatabaseTest.getTestBooking1().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestBooking1())
        );
        userRepo.getEntities().put(
                CinemaDatabaseTest.getTestUser1().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestUser1())
        );
        showTimeRepo.getEntities().put(
                CinemaDatabaseTest.getTestShowTime1().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestShowTime1())
        );
    }

    @AfterEach
    public void tearDownEach(){
        CinemaDatabaseTest.tearDown();
        bookingRepo.getEntities().clear();
        userRepo.getEntities().clear();
        showTimeRepo.getEntities().clear();
    }


    @Test
    public void insert_success(){
        ShowTime newShowTime = new ShowTime(
                CinemaDatabaseTest.getTestMovie1(),
                CinemaDatabaseTest.getTestHall1(),
                LocalDateTime.now().plusDays(1)
                );
        assertDoesNotThrow(() -> showTimeRepo.insert(newShowTime, CinemaDatabaseTest.getTestCinema1()));
        assertTrue(newShowTime.getId() > 0);
        assertTrue(showTimeRepo.getEntities().containsKey(newShowTime.getId()));
        assertEquals(newShowTime, showTimeRepo.getEntities().get(newShowTime.getId()).get());
        assertTrue(CinemaDatabaseTest.getTestCinema1().getShowTimes().contains(newShowTime));
    }



}
