package dao;

import business_logic.HallFactory;
import business_logic.exceptions.DatabaseFailedException;
import daos.MovieDao;
import daos.MovieDaoImpl;
import db.CinemaDatabaseTest;
import domain.Hall;
import domain.Movie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MovieDaoTest {

    private final MovieDao movieDao = MovieDaoImpl.getInstance(CinemaDatabaseTest.DB_URL);

    @BeforeEach
    public void setUpEach(){
        CinemaDatabaseTest.setUp();
    }

    @AfterEach
    public void tearDownEach(){
        CinemaDatabaseTest.tearDown();
    }

    @Test
    public void insertMovie_success(){
        Movie newMovie = new Movie("ABC", Duration.of(90, ChronoUnit.MINUTES));
        assertDoesNotThrow(() -> movieDao.insert(newMovie));
        Movie dbMovie = CinemaDatabaseTest.runQuery(
                "SELECT * FROM Movies WHERE movie_id = %d".formatted(newMovie.getId()),
                (res) -> {
                    if(!res.next())
                        return null;
                    Movie m = new Movie(res);
                    m.setName(res.getString("movie_name"));
                    m.setDuration(Duration.of(res.getLong("duration"), ChronoUnit.MINUTES));
                    return m;
                }
        );
        assertEquals(newMovie.getName(), dbMovie.getName());
        assertEquals(newMovie.getDuration(), dbMovie.getDuration());
    }

    @Test
    public void insertMovie_withSameName_throwsDatabaseFailedException(){
        assertThrows(DatabaseFailedException.class, () -> movieDao.insert(new Movie(CinemaDatabaseTest.getTestMovie1())));
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(*) FROM Movies WHERE movie_name = '%s'".formatted(CinemaDatabaseTest.getTestMovie1().getName()),
                (res) -> {
                    if(!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(1, count);
    }

    @Test
    public void insertMovie_withNullName_throwsDatabaseFailedException(){
        assertThrows(DatabaseFailedException.class, () -> movieDao.insert(new Movie(null, Duration.of(90, ChronoUnit.MINUTES))));
    }

    @Test
    public void updateMovie_success(){
        Movie testMovie1 = CinemaDatabaseTest.getTestMovie1();
        Movie copy = new Movie(testMovie1);
        copy.setName("ABC");
        copy.setDuration(Duration.of(100, ChronoUnit.MINUTES));
        assertDoesNotThrow(() -> movieDao.update(testMovie1, copy));
        Movie dbMovie = CinemaDatabaseTest.runQuery(
                "SELECT * FROM Movies WHERE movie_id = %d".formatted(testMovie1.getId()),
                (res) -> {
                    if(!res.next())
                        return null;
                    Movie m = new Movie(res);
                    m.setName(res.getString("movie_name"));
                    m.setDuration(Duration.of(res.getLong("duration"), ChronoUnit.MINUTES));
                    return m;
                }
        );
        assertNotNull(dbMovie);
        assertEquals(copy.getName(), dbMovie.getName());
        assertEquals(copy.getDuration(), dbMovie.getDuration());
    }

    @Test
    public void updateMovie_toSameName_throwsDatabaseFailedException(){
        Movie testMovie1 = CinemaDatabaseTest.getTestMovie1();
        Movie testMovie2 = CinemaDatabaseTest.getTestMovie2();
        Movie copy = new Movie(testMovie1);
        copy.setName(testMovie2.getName());
        assertThrows(DatabaseFailedException.class, () -> movieDao.update(testMovie1, copy));
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(*) FROM Movies WHERE movie_name = '%s'"
                        .formatted(testMovie2.getName()),
                (res) -> {
                    if (!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(1, count);
    }

    @Test
    public void updateMovie_toNullName_throwsDatabaseFailedException(){
        Movie testMovie1 = CinemaDatabaseTest.getTestMovie1();
        Movie copy = new Movie(testMovie1);
        copy.setName(null);
        assertThrows(DatabaseFailedException.class, () -> movieDao.update(testMovie1, copy));
    }

    @Test
    public void deleteMovie_success(){
        Movie testMovie1 = CinemaDatabaseTest.getTestMovie1();
        assertDoesNotThrow(() -> movieDao.delete(testMovie1));
        int count = CinemaDatabaseTest.runQuery(
                "SELECT COUNT(*) FROM Movies WHERE movie_name = '%s'"
                        .formatted(testMovie1.getName()),
                (res) -> {
                    if (!res.next())
                        return 0;
                    return res.getInt(1);
                }
        );
        assertEquals(0, count);
    }

    @Test
    public void deleteMovie_notInDatabase_throwsDatabaseFailedException(){
        assertThrows(DatabaseFailedException.class, () -> movieDao.delete(new Movie(CinemaDatabaseTest.getTestMovie1())));
    }

    @Test
    public void getMovie_success(){
        List<Movie> movies = assertDoesNotThrow(() -> movieDao.get(CinemaDatabaseTest.getTestCinema1()));
        assertEquals(CinemaDatabaseTest.getTestCinema1().getMovies().size(), movies.size());
    }

}
