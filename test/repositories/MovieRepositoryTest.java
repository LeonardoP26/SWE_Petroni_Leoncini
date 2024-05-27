package repositories;

import business_logic.exceptions.InvalidIdException;
import business_logic.repositories.*;
import dao.fake_daos.*;
import db.CinemaDatabaseTest;
import domain.Cinema;
import domain.DatabaseEntity;
import domain.Movie;
import domain.Seat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InvalidClassException;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class MovieRepositoryTest {

    private final BookingRepository bookingRepo = BookingRepositoryImpl.getInstance(new FakeBookingDao());
    private final UserRepository userRepo = UserRepositoryImpl.getInstance(new FakeUserDao(), bookingRepo);
    private final ShowTimeRepository showTimeRepo = ShowTimeRepositoryImpl.getInstance(new FakeShowTimeDao(), userRepo);
    private final SeatRepository seatRepo = SeatRepositoryImpl.getInstance(new FakeSeatDao(), userRepo);
    private final HallRepository hallRepo = HallRepositoryImpl.getInstance(new FakeHallDao(), seatRepo, showTimeRepo);
    private final CinemaRepository cinemaRepo = CinemaRepositoryImpl.getInstance(new FakeCinemaDao(), hallRepo);
    private final MovieRepository movieRepo = MovieRepositoryImpl.getInstance(new FakeMovieDao(), showTimeRepo, cinemaRepo);


    @BeforeEach
    public void seatUpEach(){
        CinemaDatabaseTest.setUp();
        movieRepo.getEntities().put(
                CinemaDatabaseTest.getTestMovie1().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestMovie1())
        );
        movieRepo.getEntities().put(
                CinemaDatabaseTest.getTestMovie2().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestMovie2())
        );
        cinemaRepo.getEntities().put(
                CinemaDatabaseTest.getTestCinema1().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestCinema1())
        );
        cinemaRepo.getEntities().put(
                CinemaDatabaseTest.getTestCinema2().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestCinema2())
        );
        hallRepo.getEntities().put(
                CinemaDatabaseTest.getTestHall1().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestHall1())
        );
        hallRepo.getEntities().put(
                CinemaDatabaseTest.getTestHall2().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestHall2())
        );
        userRepo.getEntities().put(
                CinemaDatabaseTest.getTestUser1().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestUser1())
        );
        userRepo.getEntities().put(
                CinemaDatabaseTest.getTestUser2().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestUser2())
        );
        bookingRepo.getEntities().put(
                CinemaDatabaseTest.getTestBooking1().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestBooking1())
        );
        bookingRepo.getEntities().put(
                CinemaDatabaseTest.getTestBooking2().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestBooking2())
        );
        showTimeRepo.getEntities().put(
                CinemaDatabaseTest.getTestShowTime1().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestShowTime1())
        );
        showTimeRepo.getEntities().put(
                CinemaDatabaseTest.getTestShowTime2().getId(),
                new WeakReference<>(CinemaDatabaseTest.getTestShowTime2())
        );
        for(Seat s: CinemaDatabaseTest.getTestSeats()) {
            seatRepo.getEntities().put(s.getId(), new WeakReference<>(s));
        }
    }

    @AfterEach
    public void tearDownEach(){
        CinemaDatabaseTest.tearDown();
        seatRepo.getEntities().clear();
        showTimeRepo.getEntities().clear();
        bookingRepo.getEntities().clear();
        userRepo.getEntities().clear();
        hallRepo.getEntities().clear();
        cinemaRepo.getEntities().clear();
        movieRepo.getEntities().clear();
    }

    @Test
    public void insertMovie_success(){
        Movie newMovie = new Movie("ABC", Duration.of(90, ChronoUnit.MINUTES));
        assertDoesNotThrow(() -> movieRepo.insert(newMovie));
        assertTrue(movieRepo.getEntities().containsKey(newMovie.getId()));
        assertEquals(newMovie, movieRepo.getEntities().get(newMovie.getId()).get());
    }

    @Test
    public void updateMovie_success(){
        Movie testMovie1 = CinemaDatabaseTest.getTestMovie1();
        Duration newMovieDuration = testMovie1.getDuration().plus(30, ChronoUnit.MINUTES);
        assertDoesNotThrow(() -> movieRepo.update(testMovie1, (m) -> {
            m.setName("ABC");
            m.setDuration(newMovieDuration);
        }));
        assertTrue(movieRepo.getEntities().containsKey(testMovie1.getId()));
        assertSame(testMovie1, movieRepo.getEntities().get(testMovie1.getId()).get());
        assertEquals("ABC", testMovie1.getName());
        assertEquals(newMovieDuration, testMovie1.getDuration());
    }

    @Test
    public void updateMovie_notInDatabase_throwsInvalidIdException(){
        Movie movie = new Movie("A", Duration.of(60, ChronoUnit.MINUTES));
        assertThrows(InvalidIdException.class, () -> movieRepo.update(movie, (m) -> { }));
    }

    @Test
    public void deleteMovie_success(){
        Movie testMovie1 = CinemaDatabaseTest.getTestMovie1();
        int oldId = testMovie1.getId();
        int testShowTime1Id = CinemaDatabaseTest.getTestShowTime1().getId();
        int testBooking1Id = CinemaDatabaseTest.getTestBooking1().getId();
        assertDoesNotThrow(() -> movieRepo.delete(testMovie1));
        assertFalse(movieRepo.getEntities().containsKey(oldId));
        assertEquals(testMovie1.getId(), DatabaseEntity.ENTITY_WITHOUT_ID);
        assertFalse(CinemaDatabaseTest.getTestCinema1().getMovies().contains(testMovie1));
        assertFalse(showTimeRepo.getEntities().containsKey(testShowTime1Id));
        assertEquals(CinemaDatabaseTest.getTestShowTime1().getId(), DatabaseEntity.ENTITY_WITHOUT_ID);
        assertFalse(CinemaDatabaseTest.getTestUser1().getBookings().contains(CinemaDatabaseTest.getTestBooking1()));
        assertFalse(bookingRepo.getEntities().containsKey(testBooking1Id));
        assertEquals(CinemaDatabaseTest.getTestBooking1().getId(), DatabaseEntity.ENTITY_WITHOUT_ID);
    }

    @Test
    public void deleteMovie_notInDatabase_throwsInvalidIdException(){
        assertThrows(InvalidIdException.class, () ->
                movieRepo.delete(new Movie("ABC", Duration.of(60, ChronoUnit.MINUTES)))
        );
    }

    @Test
    public void getListMovie_success(){
        List<Movie> movies = assertDoesNotThrow(() -> movieRepo.get(CinemaDatabaseTest.getTestCinema1()));
        List<Integer> movieIds = movies.stream().map(Movie::getId).toList();
        List<Movie> cachedMovies = movieRepo.getEntities().entrySet().stream().map( entrySet -> {
            if(movieIds.contains(entrySet.getKey()))
                return entrySet.getValue() != null ? entrySet.getValue().get() : null;
            return null;
        }).filter(Objects::nonNull).toList();
        assertEquals(cachedMovies.size(), movies.size());
        assertTrue(cachedMovies.containsAll(movies));
        assertTrue(CinemaDatabaseTest.getTestCinema1().getMovies().containsAll(movies));
    }

    @Test
    public void getListMovie_withCinemaNotInDatabase_throwsInvalidIdException(){
        assertThrows(InvalidIdException.class, () -> movieRepo.get(new Cinema("ABC")));
    }

    @Test
    public void getMovie_success(){
        Movie m = assertDoesNotThrow(() -> movieRepo.get(CinemaDatabaseTest.getTestMovie1()));
        assertTrue(movieRepo.getEntities().containsKey(m.getId()));
        assertSame(movieRepo.getEntities().get(m.getId()).get(), m);
    }

    @Test
    public void getMovie_notInDatabase_throwsInvalidIdException(){
        assertThrows(InvalidIdException.class, () ->
                movieRepo.get(new Movie("ABC", Duration.of(60, ChronoUnit.MINUTES)))
        );
    }

}
