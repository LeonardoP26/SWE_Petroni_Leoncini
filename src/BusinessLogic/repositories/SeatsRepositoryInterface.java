package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Movie;
import Domain.Seat;
import Domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface SeatsRepositoryInterface {

    int insert(Seat seat, int hallId) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    boolean update(@NotNull Seat seat, int hallId) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    boolean delete(@NotNull Seat seat) throws SQLException, UnableToOpenDatabaseException;

    Movie get(int seatId) throws SQLException, UnableToOpenDatabaseException;

    List<Seat> get(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException;
}
