package unit_test.dao;

import business_logic.exceptions.DatabaseFailedException;
import daos.BookingDao;
import daos.BookingDaoImpl;
import db.CinemaDatabaseTest;
import domain.Booking;
import domain.Seat;
import domain.ShowTime;
import domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookingDaoTest {

    private final BookingDao bookingDao = BookingDaoImpl.getInstance(CinemaDatabaseTest.DB_URL);

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
        ShowTime testShowTime1 = CinemaDatabaseTest.getTestShowTime1();
        User testUser1 = CinemaDatabaseTest.getTestUser1();
        ArrayList<Seat> seats = new ArrayList<>(List.of(CinemaDatabaseTest.getTestSeats().get(4)));
        Booking newBooking = new Booking( testShowTime1, seats);
        User copy = new User(testUser1);
        assertDoesNotThrow(() ->
                copy.setBalance(copy.getBalance() - (long) newBooking.getShowTime().getHall().getCost() * newBooking.getSeats().size())
        );
        assertDoesNotThrow(() -> bookingDao.insert(newBooking, testUser1, copy));
        User user = CinemaDatabaseTest.runQuery(
                "SELECT * FROM Users WHERE user_id = %d".formatted(testUser1.getId()),
                (res) -> {
                    if(!res.next())
                        return null;
                    User usr = new User(res);
                    usr.setBalance(res.getLong("balance"));
                    return usr;
                }
        );
        assertNotNull(user);
        assertEquals(copy.getBalance(), user.getBalance());
    }

    @Test
    public void insertBooking_whichAlreadyInDatabase_throwsDatabaseException() {
        ShowTime testShowTime1 = CinemaDatabaseTest.getTestShowTime1();
        User testUser1 = CinemaDatabaseTest.getTestUser1();
        ArrayList<Seat> seats = new ArrayList<>(List.of(CinemaDatabaseTest.getTestSeats().getFirst()));
        Booking newBooking = new Booking(testShowTime1, seats);
        User copy = new User(testUser1);
        assertDoesNotThrow(() ->
                copy.setBalance(copy.getBalance() - (long) newBooking.getShowTime().getHall().getCost() * newBooking.getSeats().size())
        );
        assertThrows(DatabaseFailedException.class, () -> bookingDao.insert(newBooking, testUser1, copy));
        User user = CinemaDatabaseTest.runQuery(
                "SELECT * FROM Users WHERE user_id = %d".formatted(testUser1.getId()),
                (res) -> {
                    if(!res.next())
                        return null;
                    User usr = new User(res);
                    usr.setBalance(res.getLong("balance"));
                    return usr;
                }
        );
        assertNotNull(user);
        assertEquals(testUser1.getBalance(), user.getBalance());
    }
    
    @Test
    public void insertBooking_withNullValues_throwsDatabaseFailedException(){
        Booking newbooking = new Booking(null, null);
        User copy = new User(CinemaDatabaseTest.getTestUser1());
        assertThrows(DatabaseFailedException.class, () -> bookingDao.insert(newbooking, CinemaDatabaseTest.getTestUser1(), copy));
    }

    @Test
    public void updateBooking_success(){
        Booking oldBooking = CinemaDatabaseTest.getTestBooking1();
        Booking newBooking = new Booking(oldBooking.getShowTime(), new ArrayList<>(CinemaDatabaseTest.getTestSeats().subList(4, 6)));
        long newCost = (long) oldBooking.getShowTime().getHall().getCost() * oldBooking.getSeats().size() -
                (long) newBooking.getShowTime().getHall().getCost() * newBooking.getSeats().size();
        User testUser1 = CinemaDatabaseTest.getTestUser1();
        User copy = new User(testUser1);
        assertDoesNotThrow(() -> copy.setBalance(newCost));
        assertDoesNotThrow(() -> bookingDao.update(oldBooking, newBooking, testUser1, copy));
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(seat_id) FROM Bookings WHERE booking_number = %d".formatted(oldBooking.getId()),
                (res) -> {
                    if(!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(2, count);
        User dbUser = CinemaDatabaseTest.runQuery(
                "SELECT * FROM Users WHERE user_id = %d".formatted(testUser1.getId()),
                (res) -> {
                    if(!res.next())
                        return null;
                    return new User(res);
                }
        );
        assertNotNull(dbUser);
        assertEquals(copy.getBalance(), dbUser.getBalance());
    }

    @Test
    public void updateBooking_toNullValues_throwsDatabaseFailedException(){
        Booking oldBooking = CinemaDatabaseTest.getTestBooking1();
        Booking newBooking = new Booking(oldBooking.getShowTime(), null);
        User testUser1 = CinemaDatabaseTest.getTestUser1();
        User copy = new User(testUser1);
        assertThrows(DatabaseFailedException.class, () -> bookingDao.update(oldBooking, newBooking, testUser1, copy));
    }

    @Test
    public void deleteBooking_success(){
        Booking testBooking1 = CinemaDatabaseTest.getTestBooking1();
        User testUser1 = CinemaDatabaseTest.getTestUser1();
        long oldBalance = testUser1.getBalance();
        long cost = (long) testBooking1.getShowTime().getHall().getCost() * testBooking1.getSeats().size();
        assertDoesNotThrow(() -> bookingDao.delete(CinemaDatabaseTest.getTestBooking1(), testUser1));
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(booking_number) FROM Bookings WHERE booking_number = %d".formatted(testBooking1.getId()),
                (res) -> {
                    if(!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(0, count);
        User dbUser = CinemaDatabaseTest.runQuery(
                "SELECT * FROM Users WHERE user_id = %d".formatted(testUser1.getId()),
                (res) -> {
                    if(!res.next())
                        return null;
                    User user = new User(res);
                    user.setBalance(res.getLong("balance"));
                    return user;
                }
        );
        assertNotNull(dbUser);
        assertEquals(oldBalance + cost, dbUser.getBalance());
    }

    @Test
    public void deleteBooking_notInDatabase_throwsException(){
        assertThrows(DatabaseFailedException.class, () ->
                bookingDao.delete(new Booking(CinemaDatabaseTest.getTestBooking1()), CinemaDatabaseTest.getTestUser1())
        );
    }

    @Test
    public void deleteBooking_withUserNotInDatabase_throwsException(){
        assertThrows(DatabaseFailedException.class, () ->
                bookingDao.delete(CinemaDatabaseTest.getTestBooking1(), new User(CinemaDatabaseTest.getTestUser1()))
        );
    }

    @Test
    public void getBooking_success(){
        List<Booking> bookings = assertDoesNotThrow(() -> bookingDao.get(CinemaDatabaseTest.getTestUser1()));
        assertEquals(CinemaDatabaseTest.getTestUser1().getBookings().size(), bookings.size());
    }

}
