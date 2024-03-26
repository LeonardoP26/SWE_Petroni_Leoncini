package BusinessLogic.repositories;

import BusinessLogic.UnableToOpenDatabaseException;
import Domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public interface ShowTimeRepositoryInterface {
    void insert(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException;

}
