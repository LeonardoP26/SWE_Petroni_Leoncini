package unit_test.dao;

import business_logic.exceptions.DatabaseFailedException;
import daos.ShowTimeDao;
import daos.ShowTimeDaoImpl;
import db.CinemaDatabaseTest;
import domain.ShowTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ShowTimeDaoTest {

    private final ShowTimeDao showTimeDao = ShowTimeDaoImpl.getInstance(CinemaDatabaseTest.DB_URL);

    @BeforeEach
    public void setUpEach() {
        CinemaDatabaseTest.setUp();
    }

    @AfterEach
    public void tearDownEach() {
        CinemaDatabaseTest.tearDown();
    }

    @Test
    public void insertShowTime_success() {
        ShowTime newShowTime = new ShowTime(
                CinemaDatabaseTest.getTestMovie1(),
                CinemaDatabaseTest.getTestHall2(),
                LocalDateTime.now());
        assertDoesNotThrow(() -> showTimeDao.insert(newShowTime));
        ShowTime dbShowTime = CinemaDatabaseTest.runQuery(
                "SELECT * FROM ShowTimes WHERE showtime_id = %d".formatted(newShowTime.getId()),
                (res) -> {
                    if (!res.next())
                        return null;
                    return new ShowTime(res);
                }
        );
        assertNotNull(dbShowTime);
        assertEquals(newShowTime.getId(), dbShowTime.getId());
    }

    @Test
    public void insertShowTime_withSameValues_throwsDatabaseFailedException() {
        ShowTime testShowTime1 = CinemaDatabaseTest.getTestShowTime1();
        assertThrows(DatabaseFailedException.class, () -> showTimeDao.insert(new ShowTime(testShowTime1)));
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(*) FROM ShowTimes WHERE movie_id = %d AND hall_id = %d"
                        .formatted(testShowTime1.getMovie().getId(), testShowTime1.getHall().getId()),
                (res) -> {
                    if (!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(1, count);
    }

    @Test
    public void insertShowTime_withNullName_throwsDatabaseFailedException() {
        assertThrows(DatabaseFailedException.class, () -> showTimeDao.insert(new ShowTime(null, null, null)));
    }

    @Test
    public void updateShowTime_success() {
        ShowTime testShowTime1 = CinemaDatabaseTest.getTestShowTime1();
        ShowTime copy = new ShowTime(testShowTime1);
        copy.setHall(CinemaDatabaseTest.getTestHall2());
        assertDoesNotThrow(() -> showTimeDao.update(testShowTime1, copy));
        int hallId = CinemaDatabaseTest.runQuery(
                "SELECT hall_id FROM ShowTimes WHERE showtime_id = %d".formatted(testShowTime1.getId()),
                (res) -> {
                    if (!res.next())
                        return -1;
                    return res.getInt(1);
                }
        );
        assertEquals(copy.getHall().getId(), hallId);
    }

    @Test
    public void updateShowTime_toSameValues_throwsDatabaseFailedException() {
        ShowTime testShowTime1 = CinemaDatabaseTest.getTestShowTime1();
        ShowTime testShowTime2 = CinemaDatabaseTest.getTestShowTime2();
        assertThrows(DatabaseFailedException.class, () -> showTimeDao.update(testShowTime1, testShowTime2));
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(*) FROM ShowTimes WHERE movie_id = %d AND hall_id = %d AND date = '%s'"
                        .formatted(testShowTime2.getMovie().getId(), testShowTime2.getHall().getId(), testShowTime2.getDate().toString()),
                (res) -> {
                    if (!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(1, count);
    }

    @Test
    public void updateShowTime_toNullName_throwsDatabaseFailedException() {
        ShowTime testShowTime1 = CinemaDatabaseTest.getTestShowTime1();
        ShowTime copy = new ShowTime(testShowTime1);
        copy.setMovie(null);
        copy.setHall(null);
        copy.setDate(null);
        assertThrows(DatabaseFailedException.class, () -> showTimeDao.update(testShowTime1, copy));
    }

    @Test
    public void deleteShowTime_success() {
        ShowTime testShowTime1 = CinemaDatabaseTest.getTestShowTime1();
        assertDoesNotThrow(() -> showTimeDao.delete(testShowTime1));
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(*) FROM ShowTimes WHERE movie_id = %d AND hall_id = %d AND date = '%s'"
                        .formatted(testShowTime1.getMovie().getId(), testShowTime1.getHall().getId(), testShowTime1.getDate().toString()),
                (res) -> {
                    if (!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(0, count);
    }

    @Test
    public void deleteShowTime_notInDatabase_throwsDatabaseFailedException() {
        assertThrows(DatabaseFailedException.class, () -> showTimeDao.delete(new ShowTime(CinemaDatabaseTest.getTestShowTime1())));
    }

}
