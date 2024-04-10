package daos;

import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Hall;
import Domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface HallDaoInterface {

    ResultSet insert(int hallNumber, int cinemaId, Hall.HallTypes type) throws SQLException, UnableToOpenDatabaseException;

    boolean update(int hallId, int hallNumber, int cinemaId, Hall.HallTypes type) throws SQLException, UnableToOpenDatabaseException;

    boolean delete(int hallId) throws SQLException, UnableToOpenDatabaseException;

    ResultSet get(int hallId) throws SQLException, UnableToOpenDatabaseException;

    ResultSet get(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException;
}
