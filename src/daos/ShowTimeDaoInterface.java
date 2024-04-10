package daos;

import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Movie;
import Domain.Seat;
import Domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public interface ShowTimeDaoInterface {

    ResultSet insert(int movieId, int hallId, LocalDateTime date) throws SQLException, UnableToOpenDatabaseException;

    boolean update(int showTimeId, int movieId, int hallId) throws SQLException, UnableToOpenDatabaseException;

    boolean delete(int showTimeId) throws SQLException, UnableToOpenDatabaseException;

    ResultSet get(int showTimeId) throws SQLException, UnableToOpenDatabaseException;

    ResultSet get(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException;

    boolean insertShowTimeSeat(int showTimeId, int seatId) throws SQLException, UnableToOpenDatabaseException;

    boolean updateShowTimeSeat(int showTimeId, int seatId, int bookingNumber) throws SQLException, UnableToOpenDatabaseException;
}
