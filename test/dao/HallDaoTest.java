package dao;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import daos.CinemaDao;
import daos.CinemaDaoImpl;
import daos.HallDao;
import daos.HallDaoImpl;
import db.CinemaDatabaseTest;
import domain.Cinema;
import domain.DatabaseEntity;
import domain.Hall;
import domain.ShowTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HallDaoTest {

    private final HallDao hallDao = HallDaoImpl.getInstance(CinemaDatabaseTest.DB_URL);
    private final CinemaDao cinemaDao = CinemaDaoImpl.getInstance(CinemaDatabaseTest.DB_URL);
    private Cinema testCinema;
    private Hall testHall;
    private ShowTime testShowTime;

    @BeforeEach
    public void setUpEach(){
        CinemaDatabaseTest.setUp();
        testCinema = CinemaDatabaseTest.getTestCinema1();
        testHall = CinemaDatabaseTest.getTestHall1();
        testShowTime = CinemaDatabaseTest.getTestShowTime1();
    }

    @AfterEach
    public void tearDownEach(){
        CinemaDatabaseTest.tearDown();
    }

    @Test
    public void insertHall_success(){
        Hall hall = new Hall(2);
        assertDoesNotThrow(() -> hallDao.insert(hall, testCinema));
        assertTrue(hall.getId() > 0);
        assertTrue(testCinema.getHalls().contains(hall));
    }

    @Test
    public void insertHall_withSameHallNumberAndCinemaId_throwsDatabaseFailedException(){
        Hall hall = new Hall(1);
        assertThrows(DatabaseFailedException.class, () -> hallDao.insert(hall, testCinema));
        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, hall.getId());
    }

    @Test
    public void insertHall_withNullCinema_throwsIllegalArgumentException(){
        Hall hall = new Hall(1);
        assertThrows(IllegalArgumentException.class, () -> hallDao.insert(hall, null));
        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, hall.getId());
    }

//    @Test
//    public void updateHall_success() {
//        testHall.setHallNumber(2);
//        assertDoesNotThrow(() -> hallDao.update(testHall, testCinema));
//        assertEquals(DatabaseCachingTest.get(testHall), testHall);
//    }
//
//    @Test
//    public void updateHall_toSameHallNumber_throwsDatabaseFailedException() {
//        Hall newHall = new Hall(2);
//        assertDoesNotThrow(() -> hallDao.insert(newHall, CinemaDatabaseTest.getTestCinema()));
//        newHall.setHallNumber(1);
//        assertThrows(DatabaseFailedException.class, () -> hallDao.update(newHall, testCinema));
//        assertEquals(DatabaseCachingTest.get(newHall), newHall);
//    }
//
//    @Test
//    public void updateHall_toNullCinema_throwsIllegalArgumentException(){
//        assertThrows(IllegalArgumentException.class, () -> hallDao.update(testHall, null));
//        assertEquals(DatabaseCachingTest.get(testHall), testHall);
//    }
//
//    @Test
//    public void updateHalls_withInvalidId_throwsInvalidIdException(){
//        Hall hall = new Hall(1);
//        assertThrows(InvalidIdException.class, () -> hallDao.update(hall, testCinema));
//        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, hall.getId());
//        assertNull(DatabaseCachingTest.get(hall));
//    }

    @Test
    public void deleteHall_success(){
        int oldId = testHall.getId();
        assertDoesNotThrow(() -> hallDao.delete(testHall));
        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, testHall.getId());
        assertTrue(
                () -> CinemaDatabaseTest.runQuery(
                        "SELECT * FROM Halls WHERE hall_id = %d".formatted(oldId),
                        (res) -> !res.isBeforeFirst()
                )
        );
    }

    @Test
    public void deleteHall_causeCinemaDeleted_success(){
        int oldId = testHall.getId();
        assertDoesNotThrow(() -> cinemaDao.delete(testCinema));
        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, testHall.getId());
        assertTrue(
                () -> CinemaDatabaseTest.runQuery(
                        "SELECT * FROM Halls WHERE hall_id = %d".formatted(oldId),
                        (res) -> !res.isBeforeFirst()
                )
        );
    }

    @Test
    public void deleteHall_withInvalidId_throwsInvalidIdException() {
        Hall hall = new Hall(1);
        assertThrows(InvalidIdException.class, () -> hallDao.delete(hall));
        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, hall.getId());
    }

    @Test
    public void getHall_success(){
        Hall hall = assertDoesNotThrow(() -> hallDao.get(testShowTime));
        if(testShowTime.getCinema() != null)
            assertTrue(testShowTime.getCinema().getHalls().contains(hall));
    }

    @Test
    public void getHall_withInvalidId_throwsInvalidIdException(){
        ShowTime showTime = new ShowTime(CinemaDatabaseTest.getTestMovie1(), testHall, LocalDateTime.now());
        assertThrows(InvalidIdException.class, () -> hallDao.get(showTime));
    }

    @Test
    public void getHall_withNullShowTime_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> hallDao.get(null));
    }

}
