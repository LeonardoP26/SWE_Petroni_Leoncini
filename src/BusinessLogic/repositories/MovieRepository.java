package BusinessLogic.repositories;

import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Movie;
import daos.MovieDao;
import daos.MovieDaoInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class MovieRepository implements MovieRepositoryInterface{

    private final MovieDaoInterface dao = MovieDao.getInstance();

    private static MovieRepositoryInterface instance = null;

    public static MovieRepositoryInterface getInstance() {
        if(instance == null)
            instance = new MovieRepository();
        return instance;
    }

    private MovieRepository() { }

    @Override
    public Movie getMovie(int movieId) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.getMovie(movieId)) {
            if (!res.isBeforeFirst())
                return null;
            return new Movie(res.getInt(1), res.getString(2), Duration.of(res.getLong(3), ChronoUnit.SECONDS));
        }
    }


}
