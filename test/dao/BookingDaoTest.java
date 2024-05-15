package dao;

import business_logic.exceptions.DatabaseFailedException;
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
        ArrayList<Seat> seats = new ArrayList<>(List.of(CinemaDatabaseTest.getTestSeats().get(2)));
        Booking newBooking = new Booking(testShowTime1, seats);
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
                    return new User(res);
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
                    return new User(res);
                }
        );
        assertNotNull(user);
        assertEquals(testUser1.getBalance(), user.getBalance());
    }

}
