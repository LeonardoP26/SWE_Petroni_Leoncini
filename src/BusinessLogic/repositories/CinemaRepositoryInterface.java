package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseInsertionFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Cinema;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface CinemaRepositoryInterface {


    int insert(Cinema cinema) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException;

    boolean update(@NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException;

    boolean delete(@NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException;

    Cinema get(int cinemaId) throws SQLException, UnableToOpenDatabaseException;

    List<Cinema> get() throws SQLException, UnableToOpenDatabaseException;

}
