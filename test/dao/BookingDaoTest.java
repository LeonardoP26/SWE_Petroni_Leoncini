package dao;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import daos.*;
import db.CinemaDatabaseTest;
import domain.Booking;
import domain.Seat;
import domain.ShowTime;
import domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BookingDaoTest {

    private final BookingDao bookingDao = BookingDaoImpl.getInstance(CinemaDatabaseTest.DB_URL);
    private final UserDao userDao = UserDaoImpl.getInstance(CinemaDatabaseTest.DB_URL);
    private final ShowTimeDao showTimeDao = ShowTimeDaoImpl.getInstance(CinemaDatabaseTest.DB_URL);
    private final SeatsDao seatDao = SeatsDaoImpl.getInstance(CinemaDatabaseTest.DB_URL);

    @BeforeEach
    public void setUpEach(){
        CinemaDatabaseTest.setUp();
    }

    @AfterEach
    public void tearDownEach(){
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
    public void insertBooking_withDoubleBooking_throwDatabaseFailedException(){
        assertThrows(
                DatabaseFailedException.class,
                () -> bookingDao.insert(CinemaDatabaseTest.getTestBooking(), CinemaDatabaseTest.getTestUser())
        );
    }

    @Test
    public void insertBooking_withNullValues_throwDatabaseFailedException(){
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
    public void insertBooking_withInvalidIds_throwInvalidIdException(){
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



    @Test
    public void deleteBooking_success() {
        int id = CinemaDatabaseTest.getTestBooking().getBookingNumber();
        assertDoesNotThrow(() -> bookingDao.delete(CinemaDatabaseTest.getTestBooking()));
        assertTrue(() -> CinemaDatabaseTest.runQuery(
                        "SELECT * FROM Bookings WHERE booking_number = %d".formatted(id),
                        (res) -> !res.isBeforeFirst()
                )
        );
    }

    @Test
    public void deleteBooking_causeUserDeleted_success(){
        int id = CinemaDatabaseTest.getTestBooking().getBookingNumber();
        assertDoesNotThrow(() -> userDao.delete(CinemaDatabaseTest.getTestUser()));
        assertTrue(() -> CinemaDatabaseTest.runQuery(
                        "SELECT * FROM Bookings WHERE booking_number = %d".formatted(id),
                        (res) -> !res.isBeforeFirst()
                )
        );
    }

    @Test
    public void deleteBooking_causeShowTimeDeleted_success(){
        int id = CinemaDatabaseTest.getTestBooking().getBookingNumber();
        assertDoesNotThrow(() -> showTimeDao.delete(CinemaDatabaseTest.getTestShowTime()));
        assertTrue(() -> CinemaDatabaseTest.runQuery(
                        "SELECT * FROM Bookings WHERE booking_number = %d".formatted(id),
                        (res) -> !res.isBeforeFirst()
                )
        );
    }

    @Test
    public void deleteBooking_causeSeatsDeleted_success(){
        int id = CinemaDatabaseTest.getTestBooking().getBookingNumber();
        Seat deletedSeat = CinemaDatabaseTest.getTestBooking().getSeats().getFirst();
        assertDoesNotThrow(() -> seatDao.delete(deletedSeat));
        assertTrue(() -> CinemaDatabaseTest.runQuery(
                        "SELECT * FROM Bookings WHERE booking_number = %d AND seat_id = %d".formatted(id, deletedSeat.getId()),
                        (res) -> !res.isBeforeFirst()
                )
        );
        assertTrue(() -> CinemaDatabaseTest.runQuery(
                        "SELECT * FROM Bookings WHERE booking_number = %d".formatted(id),
                        ResultSet::isBeforeFirst
                )
        );
    }

    @Test
    public void getBooking_success(){
        Booking expected = CinemaDatabaseTest.getTestBooking();
        Booking actual = assertDoesNotThrow(() -> bookingDao.get(CinemaDatabaseTest.getTestUser())).getFirst();

        assertEquals(expected.getBookingNumber(), actual.getBookingNumber());
        assertEquals(expected.getSeats().stream().map(Seat::getId).toList(), actual.getSeats().stream().map(Seat::getId).toList());
        assertEquals(expected.getShowTime().getId(), actual.getShowTime().getId());
    }

}
