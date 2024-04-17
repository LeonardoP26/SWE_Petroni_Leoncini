package daos;

import Domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public interface ShowTimeDaoInterface {

    ResultSet insert(int movieId, int hallId, LocalDateTime date) throws SQLException;

    boolean update(int showTimeId, int movieId, int hallId) throws SQLException;

    boolean delete(int showTimeId) throws SQLException;

    ResultSet get(int showTimeId) throws SQLException;

    ResultSet get(@NotNull Movie movie) throws SQLException;

//    boolean insertShowTimeSeat(int showTimeId, int seatId) throws SQLException;
//
//    boolean updateShowTimeSeat(int showTimeId, int seatId, int bookingNumber) throws SQLException;
}
