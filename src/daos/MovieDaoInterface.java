package daos;

import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface MovieDaoInterface {

    void insert(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException;

    ResultSet getMovie(int movieId) throws SQLException, UnableToOpenDatabaseException;
}
