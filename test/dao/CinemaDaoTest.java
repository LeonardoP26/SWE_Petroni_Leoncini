package dao;

import business_logic.CinemaDatabase;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import daos.CinemaDao;
import daos.CinemaDaoImpl;
import db.CinemaDatabaseTest;
import domain.Cinema;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CinemaDaoTest {

    private final CinemaDao cinemaDao = CinemaDaoImpl.getInstance(CinemaDatabase.DB_URL);

    @BeforeEach
    public void setUpEach(){
        CinemaDatabaseTest.setUp();
    }

    @AfterEach
    public void tearDownEach(){
        CinemaDatabaseTest.tearDown();
    }

    @Test
    public void insertCinema_success(){
        Cinema newCinema = new Cinema("ABC");
        assertDoesNotThrow(() -> cinemaDao.insert(newCinema));
        assertTrue(newCinema.getId() > 0);
        Cinema dbCinema = CinemaDatabaseTest.runQuery(
                "SELECT * FROM Cinemas WHERE cinema_id = %d".formatted(newCinema.getId()),
                (res) -> {
                    if(!res.next())
                        return null;
                    return new Cinema(res);
                }
        );
        assertNotNull(dbCinema);
        assertEquals(newCinema.getName(), dbCinema.getName());
    }

    @Test
    public void insertCinema_withSameName_throwsDatabaseFailedException(){
        Cinema newCinema = new Cinema(CinemaDatabaseTest.getTestCinema1().getName());
        assertThrows(DatabaseFailedException.class, () -> cinemaDao.insert(newCinema));
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(*) FROM Cinemas WHERE cinema_name = '%s'".formatted(newCinema.getName()),
                (res) -> {
                    if(!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(1, count);
    }

    @Test
    public void insertCinema_withNullName_throwsDatabaseFailedException(){
        Cinema newCinema = new Cinema(CinemaDatabaseTest.getTestCinema1().getName());
        assertThrows(DatabaseFailedException.class, () -> cinemaDao.insert(newCinema));
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(*) FROM Cinemas WHERE cinema_name = NULL",
                (res) -> {
                    if(!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(0, count);
    }

    @Test
    public void updateCinema_success(){
        Cinema testCinema1 = CinemaDatabaseTest.getTestCinema1();
        Cinema copy = new Cinema(testCinema1);
        copy.setName("ABC");
        assertDoesNotThrow(() -> cinemaDao.update(testCinema1, copy));
        Cinema dbCinema = CinemaDatabaseTest.runQuery(
                "SELECT * FROM Cinemas WHERE cinema_id = %d".formatted(testCinema1.getId()),
                (res) -> {
                    if(!res.next())
                        return null;
                    return new Cinema(res);
                }
        );
        assertNotNull(dbCinema);
        assertEquals(copy.getName(), dbCinema.getName());
    }

    @Test
    public void updateCinema_toSameName_throwsDatabaseFailedException(){
        Cinema testCinema1 = CinemaDatabaseTest.getTestCinema1();
        Cinema copy = new Cinema(testCinema1);
        copy.setName(CinemaDatabaseTest.getTestCinema2().getName());
        assertThrows(DatabaseFailedException.class, () -> cinemaDao.update(testCinema1, copy));
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(*) FROM Cinemas WHERE cinema_name = '%s'".formatted(CinemaDatabaseTest.getTestCinema2().getName()),
                (res) -> {
                    if(!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(1, count);
    }

    @Test
    public void updateCinema_toNullName_throwsDatabaseFailedException(){
        Cinema testCinema1 = CinemaDatabaseTest.getTestCinema1();
        Cinema copy = new Cinema(testCinema1);
        copy.setName(null);
        assertThrows(DatabaseFailedException.class, () -> cinemaDao.update(testCinema1, copy));
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(*) FROM Cinemas WHERE cinema_name = NULL",
                (res) -> {
                    if(!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(0, count);
    }

    @Test
    public void deleteCinema_success(){
        assertDoesNotThrow(() -> cinemaDao.delete(CinemaDatabaseTest.getTestCinema1()));
        assertTrue(() ->
            CinemaDatabaseTest.runQuery(
                    "SELECT * FROm Cinemas WHERE cinema_id = %d".formatted(CinemaDatabaseTest.getTestCinema1().getId()),
                    (res) -> !res.next()
            ));
    }

    @Test
    public void deleteCinema_notInDatabase_throwsDatabaseFailedException(){
        assertThrows(DatabaseFailedException.class, () -> cinemaDao.delete(new Cinema("ABC")));
    }

    @Test
    public void getCinema_success() {
        List<Cinema> cinemas = assertDoesNotThrow(cinemaDao::get);
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(*) FROM Cinemas",
                (res) -> {
                    if (!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(count, cinemas.size());
    }

}
