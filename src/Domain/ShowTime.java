package Domain;

import BusinessLogic.UnableToOpenDatabaseException;
import BusinessLogic.repositories.HallRepository;
import BusinessLogic.repositories.HallRepositoryInterface;
import BusinessLogic.repositories.MovieRepository;
import BusinessLogic.repositories.MovieRepositoryInterface;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ShowTime implements DatabaseEntity {

    public ShowTime(ResultSet res) throws SQLException {
        this(res.getInt(1), res.getInt(2), res.getInt(3), LocalDateTime.parse(res.getString(4), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
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


    @Override
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
