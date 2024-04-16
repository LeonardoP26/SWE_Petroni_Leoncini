package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Cinema;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface CinemaRepositoryInterface {


    int insert(Cinema cinema) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    boolean update(@NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    boolean delete(@NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException;

    Cinema get(int cinemaId) throws SQLException, UnableToOpenDatabaseException;

    List<Cinema> get() throws SQLException, UnableToOpenDatabaseException;

}
