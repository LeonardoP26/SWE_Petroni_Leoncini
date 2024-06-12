package unit_test.repositories;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.repositories.*;
import db.CinemaDatabaseTest;
import domain.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unit_test.dao.fake_daos.FakeBookingDao;
import unit_test.dao.fake_daos.FakeShowTimeDao;
import unit_test.dao.fake_daos.FakeUserDao;

import java.lang.ref.WeakReference;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
    public void insertShowTime_success(){
        ShowTime newShowTime = new ShowTime(
                CinemaDatabaseTest.getTestMovie1(),
                CinemaDatabaseTest.getTestHall1(),
                LocalDateTime.now().plusDays(1)
                );
        assertDoesNotThrow(() -> showTimeRepo.insert(newShowTime));
        assertTrue(newShowTime.getId() > 0);
        assertTrue(showTimeRepo.getEntities().containsKey(newShowTime.getId()));
        assertEquals(newShowTime, showTimeRepo.getEntities().get(newShowTime.getId()).get());
    }

    @Test
    public void insertShowTime_withInvalidIds_throwsInvalidIdException(){
        ShowTime newShowTime1 = new ShowTime(
                new Movie("ABC", Duration.of(90, ChronoUnit.MINUTES)),
                CinemaDatabaseTest.getTestHall1(),
                LocalDateTime.now().plusDays(1)
        );
        assertThrows(InvalidIdException.class, () -> showTimeRepo.insert(newShowTime1));
        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, newShowTime1.getId());
        assertFalse(showTimeRepo.getEntities().containsKey(newShowTime1.getId()));
        ShowTime newShowTime2 = new ShowTime(
                CinemaDatabaseTest.getTestMovie1(),
                new Hall(3, null),
                LocalDateTime.now().plusDays(1)
        );
        assertThrows(InvalidIdException.class, () -> showTimeRepo.insert(newShowTime2));
        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, newShowTime2.getId());
        assertFalse(showTimeRepo.getEntities().containsKey(newShowTime2.getId()));
        ShowTime newShowTime3 = new ShowTime(
                new Movie("ABC", Duration.of(90, ChronoUnit.MINUTES)),
                new Hall(3, null),
                LocalDateTime.now().plusDays(1)
        );
        assertThrows(InvalidIdException.class, () -> showTimeRepo.insert(newShowTime3));
        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, newShowTime3.getId());
        assertFalse(showTimeRepo.getEntities().containsKey(newShowTime3.getId()));
    }

    @Test
    public void updateShowTime_success(){
        ShowTime testShowTime1 = CinemaDatabaseTest.getTestShowTime1();
        LocalDateTime dateTime = LocalDateTime.now().plusHours(1);
        assertDoesNotThrow(() -> showTimeRepo.update(
                testShowTime1,
                (sht) -> {
                    sht.setMovie(CinemaDatabaseTest.getTestMovie2());
                    sht.setDate(dateTime);
                })
        );
        assertEquals(CinemaDatabaseTest.getTestMovie2(), testShowTime1.getMovie());
        assertEquals(dateTime, testShowTime1.getDate());
    }

    @Test
    public void updateShowTime_withInvalidIds_throwInvalidIdException(){
        ShowTime testShowTime1 = CinemaDatabaseTest.getTestShowTime1();
        int showTimeId = testShowTime1.getId();
        assertThrows(InvalidIdException.class, () -> showTimeRepo.update(
                new ShowTime(null, null, null),
                (sht) -> { }
        ));
        assertEquals(showTimeId, testShowTime1.getId());

        ShowTime testShowTime2 = CinemaDatabaseTest.getTestShowTime1();
        Movie oldMovie = testShowTime2.getMovie();
        showTimeId = testShowTime2.getId();
        assertThrows(InvalidIdException.class, () -> showTimeRepo.update(
                testShowTime2,
                (sht) -> sht.setMovie(new Movie("ABC", Duration.of(90, ChronoUnit.MINUTES))))
        );
        assertEquals(showTimeId, testShowTime2.getId());
        assertEquals(oldMovie, testShowTime2.getMovie());

        ShowTime testShowTime3 = CinemaDatabaseTest.getTestShowTime1();
        Hall oldHall = testShowTime3.getHall();
        showTimeId = testShowTime3.getId();
        assertThrows(InvalidIdException.class, () -> showTimeRepo.update(
                testShowTime3,
                (sht) -> sht.setHall(new Hall(3, null)))
        );
        assertEquals(showTimeId, testShowTime3.getId());
        assertEquals(oldHall, testShowTime3.getHall());
    }

    @Test
    public void updateShowTime_InvalidValues_throwsDatabaseFailedValues(){
        ShowTime testShowTime1 = CinemaDatabaseTest.getTestShowTime1();
        testShowTime1.setHall(null);
        assertThrows(DatabaseFailedException.class, () -> showTimeRepo.update(
                        testShowTime1,
                        (sht) -> { }
                )
        );

        ShowTime testShowTime2 = CinemaDatabaseTest.getTestShowTime1();
        testShowTime2.setMovie(null);
        assertThrows(DatabaseFailedException.class, () -> showTimeRepo.update(
                        CinemaDatabaseTest.getTestShowTime1(),
                        (sht) -> sht.setHall(CinemaDatabaseTest.getTestHall2())
                )
        );

    }

    @Test
    public void deleteShowTime_success(){
        ShowTime testShowTime = CinemaDatabaseTest.getTestShowTime1();
        User testUser = CinemaDatabaseTest.getTestUser1();
        long oldBalance = testUser.getBalance();
        Booking testBooking = CinemaDatabaseTest.getTestBooking1();
        assertDoesNotThrow(() -> showTimeRepo.delete(testShowTime));
        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, testShowTime.getId());
        assertFalse(testUser.getBookings().contains(testBooking));
        assertEquals(oldBalance + testBooking.getCost(), testUser.getBalance());
        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, testBooking.getId());
    }

    @Test
    public void deleteShowTime_notInDatabase_throwsInvalidIdException(){
        ShowTime newShowTime = new ShowTime(null, null, null);
        assertThrows(InvalidIdException.class, () -> showTimeRepo.delete(newShowTime));
    }

    @Test
    public void getShowTime_success(){
        List<ShowTime> showTimes = assertDoesNotThrow(() ->
                showTimeRepo.get(CinemaDatabaseTest.getTestMovie1(), CinemaDatabaseTest.getTestCinema1())
        );
        for(ShowTime sht : showTimes){
            assertTrue(showTimeRepo.getEntities().containsKey(sht.getId()));
            assertEquals(showTimeRepo.getEntities().get(sht.getId()).get(), sht);
        }
    }

    @Test
    public void getShowTime_withMovieNotInDatabase_throwsInvalidIdException(){
        assertThrows(InvalidIdException.class, () ->
                showTimeRepo.get(
                        new Movie("ABC", Duration.of(90, ChronoUnit.MINUTES)),
                        CinemaDatabaseTest.getTestCinema1()
                )
        );
    }

}
