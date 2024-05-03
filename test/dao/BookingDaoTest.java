package dao;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import daos.BookingDao;
import daos.BookingDaoImpl;
import db.CinemaDatabaseTest;
import domain.Booking;
import domain.Seat;
import domain.ShowTime;
import domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class BookingDaoTest {

    private final BookingDao bookingDao = BookingDaoImpl.getInstance(CinemaDatabaseTest.DB_URL);

    @BeforeEach
    public void setUpEach(){
        CinemaDatabaseTest.setUp();
    }

    @AfterEach
    public void tearDown(){
        CinemaDatabaseTest.tearDown();
    }

    @Test
    public void insertBooking_success(){
        ArrayList<Seat> seats = CinemaDatabaseTest.getTestSeats();
        seats = new ArrayList<>(seats.subList(seats.size() - 3, seats.size() - 1));
        Booking newBooking = new Booking(CinemaDatabaseTest.getTestShowTime(), seats);
        assertDoesNotThrow(() -> bookingDao.insert(newBooking, CinemaDatabaseTest.getTestUser()));
    }

    @Test
    public void insertBooking_doubleBooking_throwException(){
        assertThrows(
                DatabaseFailedException.class,
                () -> bookingDao.insert(CinemaDatabaseTest.getTestBooking(), CinemaDatabaseTest.getTestUser())
        );
    }

    @Test
    public void insertBooking_nullValues_throwException(){
        Booking nullValuesBooking = CinemaDatabaseTest.getTestBooking();
        ShowTime oldShowTime = nullValuesBooking.getShowTime();
        nullValuesBooking.setShowTime(null);
        assertThrows(
                DatabaseFailedException.class,
                () -> bookingDao.insert(nullValuesBooking, CinemaDatabaseTest.getTestUser())
        );
        nullValuesBooking.setShowTime(oldShowTime);
        ArrayList<Seat> oldSeats = nullValuesBooking.getSeats();
        nullValuesBooking.setSeats(null);
        assertThrows(
                DatabaseFailedException.class,
                () -> bookingDao.insert(nullValuesBooking, CinemaDatabaseTest.getTestUser())
        );
        nullValuesBooking.setSeats(oldSeats);
        assertThrows(
                DatabaseFailedException.class,
                () -> bookingDao.insert(nullValuesBooking, null)
        );
    }

    @Test
    public void insertBooking_invalidIds_throwException(){
        Booking testBooking = CinemaDatabaseTest.getTestBooking();
        ShowTime oldShowTime = testBooking.getShowTime();
        testBooking.setShowTime(new ShowTime(oldShowTime.getMovie(), oldShowTime.getHall(), oldShowTime.getDate()));
        assertThrows(InvalidIdException.class, () -> bookingDao.insert(testBooking, CinemaDatabaseTest.getTestUser()));
        testBooking.setShowTime(oldShowTime);
        ArrayList<Seat> oldSeats = testBooking.getSeats();
        testBooking.setSeats(new ArrayList<>(oldSeats.stream().map((s) -> new Seat(s.getRow(), s.getNumber())).toList()));
        assertThrows(InvalidIdException.class, () -> bookingDao.insert(testBooking, CinemaDatabaseTest.getTestUser()));
        testBooking.setSeats(oldSeats);
        User oldUser = CinemaDatabaseTest.getTestUser();
        User invalidIdUser = new User(oldUser.getUsername(), oldUser.getPassword());
        assertThrows(InvalidIdException.class, () -> bookingDao.insert(testBooking, invalidIdUser));
    }


}
