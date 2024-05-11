package dao;

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
    private final SeatDao seatDao = SeatDaoImpl.getInstance(CinemaDatabaseTest.DB_URL);

    @BeforeEach
    public void setUpEach(){
        CinemaDatabaseTest.setUp();
    }

    @AfterEach
    public void tearDownEach(){
        CinemaDatabaseTest.tearDown();
    }

    @Test
    public void insertBooking_success() {
        ArrayList<Seat> seats = CinemaDatabaseTest.getTestSeats();
        seats = new ArrayList<>(seats.subList(seats.size() - 3, seats.size() - 1));
        Booking newBooking = new Booking(CinemaDatabaseTest.getTestShowTime1(), seats);
    }

    @Test
    public void insertBooking_withDoubleBooking_throwDatabaseFailedException(){
    }

    @Test
    public void insertBooking_withNullValues_throwDatabaseFailedException(){
        Booking nullValuesBooking = CinemaDatabaseTest.getTestBooking1();
        ShowTime oldShowTime = nullValuesBooking.getShowTime();
        nullValuesBooking.setShowTime(null);
        ArrayList<Seat> oldSeats = nullValuesBooking.getSeats();
        nullValuesBooking.setSeats(null);
        nullValuesBooking.setSeats(oldSeats);
    }

    @Test
    public void insertBooking_withInvalidIds_throwInvalidIdException(){
        Booking testBooking = CinemaDatabaseTest.getTestBooking1();
        ShowTime oldShowTime = testBooking.getShowTime();
        testBooking.setShowTime(new ShowTime(oldShowTime.getMovie(), oldShowTime.getHall(), oldShowTime.getDate()));

        testBooking.setShowTime(oldShowTime);
        ArrayList<Seat> oldSeats = testBooking.getSeats();
        testBooking.setSeats(new ArrayList<>(oldSeats.stream().map((s) -> new Seat(s.getRow(), s.getNumber())).toList()));

        testBooking.setSeats(oldSeats);
        User oldUser = CinemaDatabaseTest.getTestUser1();
        User invalidIdUser = new User(oldUser.getUsername(), oldUser.getPassword());

    }



    @Test
    public void deleteBooking_success() {
        int id = CinemaDatabaseTest.getTestBooking1().getBookingNumber();
        assertTrue(() -> CinemaDatabaseTest.runQuery(
                        "SELECT * FROM Bookings WHERE booking_number = %d".formatted(id),
                        (res) -> !res.isBeforeFirst()
                )
        );
    }

    @Test
    public void deleteBooking_causeUserDeleted_success(){
        int id = CinemaDatabaseTest.getTestBooking1().getBookingNumber();
        assertDoesNotThrow(() -> userDao.delete(CinemaDatabaseTest.getTestUser1()));
        assertTrue(() -> CinemaDatabaseTest.runQuery(
                        "SELECT * FROM Bookings WHERE booking_number = %d".formatted(id),
                        (res) -> !res.isBeforeFirst()
                )
        );
    }

    @Test
    public void deleteBooking_causeShowTimeDeleted_success(){
        int id = CinemaDatabaseTest.getTestBooking1().getBookingNumber();
        assertDoesNotThrow(() -> showTimeDao.delete(CinemaDatabaseTest.getTestShowTime1()));
        assertTrue(() -> CinemaDatabaseTest.runQuery(
                        "SELECT * FROM Bookings WHERE booking_number = %d".formatted(id),
                        (res) -> !res.isBeforeFirst()
                )
        );
    }

    @Test
    public void deleteBooking_causeSeatsDeleted_success(){
        int id = CinemaDatabaseTest.getTestBooking1().getBookingNumber();
        Seat deletedSeat = CinemaDatabaseTest.getTestBooking1().getSeats().getFirst();
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
        Booking expected = CinemaDatabaseTest.getTestBooking1();
        Booking actual = assertDoesNotThrow(() -> bookingDao.get(CinemaDatabaseTest.getTestUser1())).getFirst();
        assertEquals(expected.getBookingNumber(), actual.getBookingNumber());
        assertEquals(expected.getSeats().stream().map(Seat::getId).toList(), actual.getSeats().stream().map(Seat::getId).toList());
        assertEquals(expected.getShowTime().getId(), actual.getShowTime().getId());
    }

}
