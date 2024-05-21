package dao;

import business_logic.exceptions.DatabaseFailedException;
import daos.SeatDao;
import daos.SeatDaoImpl;
import db.CinemaDatabaseTest;
import domain.Seat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SeatDaoTest {

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
    public void insertSeat_success(){
        Seat newSeat = new Seat('a', 4);
        assertDoesNotThrow(() -> seatDao.insert(newSeat, CinemaDatabaseTest.getTestHall1()));
        Seat dbMovie = CinemaDatabaseTest.runQuery(
                "SELECT * FROM Seats WHERE seat_id = %d".formatted(newSeat.getId()),
                (res) -> {
                    if(!res.next())
                        return null;
                    Seat seat = new Seat(res);
                    seat.setRow(res.getString("row").charAt(0));
                    seat.setNumber(res.getInt("number"));
                    return seat;
                }
        );
        assertEquals(newSeat.getRow(), dbMovie.getRow());
        assertEquals(newSeat.getNumber(), dbMovie.getNumber());
        assertEquals(newSeat.isBooked(), dbMovie.isBooked());
    }

    @Test
    public void insertSeat_withSameSameRowAndNumber_throwsDatabaseFailedException(){
        assertThrows(DatabaseFailedException.class, () -> seatDao.insert(new Seat('a', 1), CinemaDatabaseTest.getTestHall1()));
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(*) FROM Seats WHERE row = '%s' AND number = %d AND hall_id = %d"
                        .formatted('a', 1, CinemaDatabaseTest.getTestHall1().getId()),
                (res) -> {
                    if(!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(1, count);
    }

    @Test
    public void updateSeat_success(){
        Seat testSeat = CinemaDatabaseTest.getTestSeats().getFirst();
        Seat copy = new Seat(testSeat);
        copy.setRow('c');
        copy.setNumber(4);
        copy.setBooked(true);
        assertDoesNotThrow(() -> seatDao.update(testSeat, copy, CinemaDatabaseTest.getTestHall1()));
        Seat dbMovie = CinemaDatabaseTest.runQuery(
                "SELECT * FROM Seats WHERE seat_id = %d".formatted(testSeat.getId()),
                (res) -> {
                    if(!res.next())
                        return null;
                    Seat seat = new Seat(res);
                    seat.setRow(res.getString("row").charAt(0));
                    seat.setNumber(res.getInt("number"));
                    return seat;
                }
        );
        assertNotNull(dbMovie);
        assertEquals(copy.getRow(), dbMovie.getRow());
        assertEquals(copy.getNumber(), dbMovie.getNumber());
    }

    @Test
    public void updateSeat_toSameRowAndNumber_throwsDatabaseFailedException(){
        Seat testSeat = CinemaDatabaseTest.getTestSeats().getFirst();
        Seat copy = new Seat(testSeat);
        copy.setRow('a');
        copy.setNumber(2);
        assertThrows(DatabaseFailedException.class, () -> seatDao.update(testSeat, copy, CinemaDatabaseTest.getTestHall1()));
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(*) FROM Seats WHERE row = '%s' AND number = %d AND hall_id = %d"
                        .formatted(copy.getRow(), copy.getNumber(), CinemaDatabaseTest.getTestHall1().getId()),
                (res) -> {
                    if (!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(1, count);
    }

    @Test
    public void deleteSeat_success(){
        Seat testSeat = CinemaDatabaseTest.getTestSeats().getFirst();
        assertDoesNotThrow(() -> seatDao.delete(testSeat));
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(*) FROM Seats WHERE row = '%s' AND number = %d AND hall_id = %d"
                        .formatted(testSeat.getRow(), testSeat.getNumber(), CinemaDatabaseTest.getTestHall1().getId()),
                (res) -> {
                    if (!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(0, count);
    }

    @Test
    public void deleteSeat_notInDatabase_throwsDatabaseFailedException(){
        assertThrows(DatabaseFailedException.class, () -> seatDao.delete(new Seat(CinemaDatabaseTest.getTestSeats().getFirst())));
    }

    @Test
    public void getSeat_success(){
        List<Seat> seats = assertDoesNotThrow(() -> seatDao.get(CinemaDatabaseTest.getTestShowTime1()));
        assertEquals(CinemaDatabaseTest.getTestShowTime1().getHall().getSeats().size(), seats.size());
    }

}
