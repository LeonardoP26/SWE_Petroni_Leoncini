package Domain;

import BusinessLogic.UnableToOpenDatabaseException;
import BusinessLogic.repositories.HallRepository;
import BusinessLogic.repositories.HallRepositoryInterface;
import BusinessLogic.repositories.MovieRepository;
import BusinessLogic.repositories.MovieRepositoryInterface;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ShowTime {

    public ShowTime(int id, int movieId, int hallId, String date){
        this(id, movieId, hallId, LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public ShowTime(int id, int movieId, int hallId, LocalDateTime date){
        this.id = id;
        this.movieId = movieId;
        this.hallId = hallId;
        this.date = date.truncatedTo(ChronoUnit.MINUTES);
    }

    private final int id;
    private final int movieId;
    private final LocalDateTime date;
    private final int hallId;
    private final HallRepositoryInterface hallRepo = HallRepository.getInstance();
    private final MovieRepositoryInterface movieRepo = MovieRepository.getInstance();


    public int getId() {
        return id;
    }

    public int getMovieId() {
        return movieId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getHallId() {
        return hallId;
    }

    public Hall getHall() throws SQLException, UnableToOpenDatabaseException {
        return hallRepo.getHall(hallId);
    }

    public Movie getMovie() throws SQLException, UnableToOpenDatabaseException {
        return movieRepo.getMovie(movieId);
    }

}
