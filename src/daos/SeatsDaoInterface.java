package daos;

import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Seat;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public interface SeatsDaoInterface {

    void insert(@NotNull Seat seat) throws SQLException, UnableToOpenDatabaseException;

    void update(@NotNull Seat seat) throws SQLException, UnableToOpenDatabaseException;

    Seat getSeat(int id) throws SQLException, UnableToOpenDatabaseException;

}
