package daos;

import domain.Hall;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface HallDaoInterface {

    ResultSet insert(int hallNumber, int cinemaId, Hall.HallTypes type) throws SQLException;

    boolean update(int hallId, int hallNumber, int cinemaId, Hall.HallTypes type) throws SQLException;

    boolean delete(int hallId) throws SQLException;

    ResultSet get(int hallId) throws SQLException;

    ResultSet get(@NotNull ShowTime showTime) throws SQLException;
}
