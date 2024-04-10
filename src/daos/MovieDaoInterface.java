package daos;

import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Cinema;
import Domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

public interface MovieDaoInterface {

    ResultSet insert(String movieName, Duration movieDuration) throws SQLException, UnableToOpenDatabaseException;

    boolean update(int movieId, String movieName, Duration movieDuration) throws SQLException, UnableToOpenDatabaseException;

    boolean delete(int movieId) throws SQLException, UnableToOpenDatabaseException;

    ResultSet get(int movieId) throws SQLException, UnableToOpenDatabaseException;

    ResultSet get(@NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException;
}
