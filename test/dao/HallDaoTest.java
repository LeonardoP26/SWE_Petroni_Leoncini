package dao;

import business_logic.HallFactory;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import daos.CinemaDao;
import daos.CinemaDaoImpl;
import daos.HallDao;
import daos.HallDaoImpl;
import db.CinemaDatabaseTest;
import domain.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HallDaoTest {

    private final HallDao hallDao = HallDaoImpl.getInstance(CinemaDatabaseTest.DB_URL);

    @BeforeEach
    public void setUpEach(){
        CinemaDatabaseTest.setUp();
    }

    @AfterEach
    public void tearDownEach(){
        CinemaDatabaseTest.tearDown();
    }

    @Test
    public void insertHall_success(){
        Hall newHall = new ImaxHall(CinemaDatabaseTest.getTestHall1().getHallNumber() + 1);
        assertDoesNotThrow(() -> hallDao.insert(newHall, CinemaDatabaseTest.getTestCinema1()));
        assertTrue(newHall.getId() > 0);
        Hall dbHall = CinemaDatabaseTest.runQuery(
                "SELECT * FROM Halls WHERE hall_id = %d".formatted(newHall.getId()),
                (res) -> {
                    if(!res.next())
                        return null;
                    return HallFactory.createHall(res);
                }
        );
        assertNotNull(dbHall);
        assertEquals(dbHall.getHallNumber(), newHall.getHallNumber());
        assertEquals(dbHall.getHallType(), newHall.getHallType());
    }

    @Test
    public void insertHall_withSameHallNumber_throwsDatabaseException(){
        Hall newHall = new Hall(CinemaDatabaseTest.getTestHall1().getHallNumber());
        assertThrows(DatabaseFailedException.class, () -> hallDao.insert(newHall, CinemaDatabaseTest.getTestCinema1()));
        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, newHall.getId());
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(hall_number) FROM Halls WHERE hall_number = %d AND cinema_id = %d"
                        .formatted(newHall.getHallNumber(), CinemaDatabaseTest.getTestCinema1().getId()),
                (res) -> {
                    if (!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(1, count);
    }

    @Test
    public void updateHall_success(){
        Hall testHall1 = CinemaDatabaseTest.getTestHall1();
        Hall copy = HallFactory.createHall(testHall1);
        copy.setHallNumber(testHall1.getHallNumber() + 1);
        assertDoesNotThrow(() -> hallDao.update(testHall1, copy, CinemaDatabaseTest.getTestCinema1()));
        Hall dbHall = CinemaDatabaseTest.runQuery(
                "SELECT * FROM Halls WHERE hall_id = %d".formatted(testHall1.getId()),
                (res) -> {
                    if(!res.next())
                        return null;
                    return HallFactory.createHall(res);
                }
        );
        assertNotNull(dbHall);
        assertEquals(copy.getHallNumber(), dbHall.getHallNumber());
    }

    @Test
    public void updateHall_toSameHallNumber_throwsDatabaseFailedException(){
        Hall testHall1 = CinemaDatabaseTest.getTestHall1();
        Hall copy = HallFactory.createHall(testHall1);
        assertThrows(DatabaseFailedException.class, () -> hallDao.update(testHall1, copy, CinemaDatabaseTest.getTestCinema2()));
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(*) FROM Halls WHERE hall_number = %d AND cinema_id = %d"
                        .formatted(testHall1.getHallNumber(), CinemaDatabaseTest.getTestCinema2().getId()),
                (res) -> {
                    if (!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(1, count);
    }

    @Test
    public void deleteHall_success(){
        Hall testHall1 = CinemaDatabaseTest.getTestHall1();
        assertDoesNotThrow(() -> hallDao.delete(testHall1));
        Hall dbHall = CinemaDatabaseTest.runQuery(
        "SELECT * FROM Halls WHERE hall_id = %d"
                .formatted(testHall1.getId()),
                (res) -> {
                    if (!res.next())
                        return null;
                    return HallFactory.createHall(res);
                }
        );
        assertNull(dbHall);
    }

    @Test
    public void deleteHall_notInDatabase_throwsDatabaseFailedException(){
        assertThrows(DatabaseFailedException.class, () -> hallDao.delete(HallFactory.createHall(CinemaDatabaseTest.getTestHall1())));
    }

    @Test
    public void getHall_success(){
        ShowTime testShowTime1 = CinemaDatabaseTest.getTestShowTime1();
        Hall hall = hallDao.get(testShowTime1, CinemaDatabaseTest.getTestCinema1());
        Hall dbHall = CinemaDatabaseTest.runQuery(
                "SELECT * FROM Halls JOIN ShowTimes ON Halls.hall_id = ShowTimes.hall_id WHERE showtime_id = %d"
                        .formatted(testShowTime1.getId()),
                (res) -> {
                    if (!res.next())
                        return null;
                    return HallFactory.createHall(res);
                }
        );
        assertEquals(hall.getClass(), dbHall.getClass());
        assertEquals(hall.getId(), dbHall.getId());
        assertEquals(hall.getHallNumber(), dbHall.getHallNumber());
    }

}
