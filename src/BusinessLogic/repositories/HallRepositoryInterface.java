package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseInsertionFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Cinema;
import Domain.Hall;
import Domain.Seat;
import Domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface HallRepositoryInterface {


    int insert(@NotNull Hall hall, int cinemaId) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException;

    boolean update(@NotNull Hall hall, int cinemaId) throws SQLException, UnableToOpenDatabaseException;

    boolean delete(@NotNull Hall hall) throws SQLException, UnableToOpenDatabaseException;

    Hall get(int hallId) throws SQLException, UnableToOpenDatabaseException;

    Hall get(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException;
}
