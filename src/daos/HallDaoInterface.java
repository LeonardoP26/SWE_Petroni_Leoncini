package daos;

import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Hall;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface HallDaoInterface {

    void insert(@NotNull Hall hall) throws SQLException, UnableToOpenDatabaseException;

    ResultSet getHall(int hallId) throws SQLException, UnableToOpenDatabaseException;

    ResultSet getHallSeats(Hall hall) throws SQLException, UnableToOpenDatabaseException;
}
