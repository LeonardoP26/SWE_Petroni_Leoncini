package BusinessLogic.repositories;

import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Movie;
import Domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface ShowTimeRepositoryInterface {
    void insert(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException;

}
