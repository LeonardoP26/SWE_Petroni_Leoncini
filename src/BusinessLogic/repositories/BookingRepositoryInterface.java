package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import Domain.*;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface BookingRepositoryInterface {

    int insert(@NotNull Booking booking, List<User> users) throws DatabaseFailedException;

    boolean delete(@NotNull Booking booking);

    List<Booking> get(@NotNull User user);
}
