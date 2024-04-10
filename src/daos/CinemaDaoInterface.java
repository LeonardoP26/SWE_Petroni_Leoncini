package daos;

import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Cinema;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface CinemaDaoInterface {

    ResultSet insert(String cinemaName) throws SQLException, UnableToOpenDatabaseException;

    boolean update(int cinemaId, String cinemaName) throws SQLException, UnableToOpenDatabaseException;

    boolean delete(int cinemaId) throws SQLException, UnableToOpenDatabaseException;

    ResultSet get(int cinemaId) throws SQLException, UnableToOpenDatabaseException;

    ResultSet get() throws SQLException, UnableToOpenDatabaseException;

}
