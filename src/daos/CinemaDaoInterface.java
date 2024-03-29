package daos;

import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Cinema;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface CinemaDaoInterface {

    void insert(Cinema cinema) throws SQLException, UnableToOpenDatabaseException;

    ResultSet getCinemaHalls(Cinema cinema) throws SQLException, UnableToOpenDatabaseException;

    ResultSet getCinemaMovies(Cinema cinema) throws SQLException, UnableToOpenDatabaseException;
}
