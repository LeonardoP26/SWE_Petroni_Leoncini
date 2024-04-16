package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Cinema;
import Domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface MovieRepositoryInterface {

    int insert(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    boolean update(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    boolean delete(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException;

    Movie get(int movieId) throws SQLException, UnableToOpenDatabaseException;

    List<Movie> get(Cinema cinema) throws SQLException, UnableToOpenDatabaseException;
}
