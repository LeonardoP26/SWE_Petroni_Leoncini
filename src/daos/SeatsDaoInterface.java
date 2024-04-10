package daos;

import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Seat;
import Domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SeatsDaoInterface {

    ResultSet insert(char row, int number, int hallId) throws SQLException, UnableToOpenDatabaseException;

    boolean update(int seatId, char row, int number, int hallId) throws SQLException, UnableToOpenDatabaseException;

    boolean delete(int seatId) throws SQLException, UnableToOpenDatabaseException;

    ResultSet get(int seatId) throws SQLException, UnableToOpenDatabaseException;

    ResultSet get(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException;
}
