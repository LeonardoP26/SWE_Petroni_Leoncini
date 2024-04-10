package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseInsertionFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Cinema;
import Domain.Movie;
import Domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface MovieRepositoryInterface {

    int insert(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException;

    boolean update(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException;

    boolean delete(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException;

    Movie get(int movieId) throws SQLException, UnableToOpenDatabaseException;

    List<Movie> get(Cinema cinema) throws SQLException, UnableToOpenDatabaseException;
}
