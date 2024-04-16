package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Movie;
import Domain.Seat;
import Domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface ShowTimeRepositoryInterface {
    int insert(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    boolean update(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    boolean delete(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException;

    ShowTime get(int showTimeId) throws SQLException, UnableToOpenDatabaseException;

    List<ShowTime> get(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException;

    void insertShowTimeSeat(int showTimeId, int seatId) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    boolean updateShowTimeSeat(ShowTime showTime, Seat seat, int bookingNumber) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;
}
