package daos;

import Domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SeatsDaoInterface {

    ResultSet insert(char row, int number, int hallId) throws SQLException;

    boolean update(int seatId, char row, int number, int hallId) throws SQLException;

    boolean delete(int seatId) throws SQLException;

    ResultSet get(int seatId) throws SQLException;

    ResultSet get(@NotNull ShowTime showTime) throws SQLException;
}
