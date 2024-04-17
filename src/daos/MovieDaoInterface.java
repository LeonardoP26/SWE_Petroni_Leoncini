package daos;

import Domain.Cinema;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

public interface MovieDaoInterface {

    ResultSet insert(String movieName, Duration movieDuration) throws SQLException;

    boolean update(int movieId, String movieName, Duration movieDuration) throws SQLException;

    boolean delete(int movieId) throws SQLException;

    ResultSet get(int movieId) throws SQLException;

    ResultSet get(@NotNull Cinema cinema) throws SQLException;
}
