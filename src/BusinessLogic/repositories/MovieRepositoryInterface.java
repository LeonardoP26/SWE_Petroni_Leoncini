package BusinessLogic.repositories;

import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Movie;

import java.sql.SQLException;

public interface MovieRepositoryInterface {
    Movie getMovie(int movieId) throws SQLException, UnableToOpenDatabaseException;
}
