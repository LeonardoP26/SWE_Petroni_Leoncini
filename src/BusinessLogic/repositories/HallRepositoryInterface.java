package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Hall;
import Domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public interface HallRepositoryInterface {


    int insert(@NotNull Hall hall, int cinemaId) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    boolean update(@NotNull Hall hall, int cinemaId) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    boolean delete(@NotNull Hall hall) throws SQLException, UnableToOpenDatabaseException;

    Hall get(int hallId) throws SQLException, UnableToOpenDatabaseException;

    Hall get(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException;
}
