package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.*;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface BookingRepositoryInterface {

    int insert(@NotNull Booking booking, List<User> users) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    boolean delete(@NotNull Booking booking) throws SQLException, UnableToOpenDatabaseException;

    List<Booking> get(@NotNull User user) throws SQLException, UnableToOpenDatabaseException;
}
