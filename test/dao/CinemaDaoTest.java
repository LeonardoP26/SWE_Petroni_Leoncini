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
        Cinema actual = new Cinema("cinema2");
        assertDoesNotThrow(() -> cinemaDao.insert(actual));
        assertTrue(actual.getId() > 0);
    }

    @Test
    public void insertCinema_withSameName_throwsDatabaseFailedException(){
        Cinema actual = new Cinema("cinema1");
        assertThrows(DatabaseFailedException.class, () -> cinemaDao.insert(actual));
    }

    @Test
    public void insertCinema_withNullName_throwsDatabaseFailedException(){
        Cinema cinema = new Cinema((String) null);
        assertThrows(DatabaseFailedException.class, () -> cinemaDao.insert(cinema));
    }

//    @Test
//    public void updateCinema_success(){
//        Cinema expected = CinemaDatabaseTest.getTestCinema1();
//        expected.setName("cinema");
//        assertDoesNotThrow(() -> cinemaDao.update(expected));
//        Cinema actual = CinemaDatabaseTest.runQuery(
//                "SELECT * FROM Cinemas WHERE cinema_name = '%s'".formatted(expected.getName()),
//                Cinema::new
//        );
//        assertEquals(expected.getId(), actual.getId());
//        assertEquals(expected.getName(), actual.getName());
//    }
//
//    @Test
//    public void updateCinema_toSameName_throwsDatabaseFailedException(){
//        Cinema cinema2 = new Cinema("cinema2");
//        assertDoesNotThrow(() -> cinemaDao.insert(cinema2));
//        cinema2.setName("cinema1");
//        assertThrows(DatabaseFailedException.class, () -> cinemaDao.update(cinema2));
//    }
//
//    @Test
//    public void updateCinema_toNullName_throwsDatabaseFailedException(){
//        Cinema cinema = CinemaDatabaseTest.getTestCinema1();
//        cinema.setName(null);
//        assertThrows(DatabaseFailedException.class, () -> cinemaDao.update(cinema));
//    }
//
//    @Test
//    public void updateCinema_withInvalidId_throwsInvalidIdException(){
//        Cinema newCinema = new Cinema("A");
//        assertThrows(InvalidIdException.class, () -> cinemaDao.update(newCinema));
//    }

    @Test
    public void getCinema_success(){
        Cinema actual = assertDoesNotThrow(cinemaDao::get).getFirst();
        Cinema expected = CinemaDatabaseTest.getTestCinema1();
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    public void deleteCinema_success(){
        int id = CinemaDatabaseTest.getTestCinema1().getId();
        assertDoesNotThrow(() -> cinemaDao.delete(CinemaDatabaseTest.getTestCinema1()));
        assertTrue(
                () -> CinemaDatabaseTest.runQuery(
                        "SELECT * FROM Cinemas WHERE cinema_id = %d".formatted(id),
                        (res) -> !res.isBeforeFirst()
                )
        );
    }

    @Test
    public void deleteCinema_withInvalidId_throwsInvalidIdException(){
        Cinema newCinema = new Cinema("A");
        assertThrows(InvalidIdException.class, () -> cinemaDao.delete(newCinema));
        assertTrue(
                () -> CinemaDatabaseTest.runQuery(
                        "SELECT * FROM Cinemas WHERE cinema_id = '%s'".formatted(newCinema.getName()),
                        (res) -> !res.isBeforeFirst()
                )
        );
    }

}
