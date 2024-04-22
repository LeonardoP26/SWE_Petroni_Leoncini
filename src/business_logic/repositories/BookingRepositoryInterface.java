package business_logic.repositories;

import business_logic.exceptions.DatabaseFailedException;
import domain.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface BookingRepositoryInterface {

    void insert(@NotNull Booking booking, User user) throws DatabaseFailedException;

    void delete(@NotNull Booking booking) throws DatabaseFailedException;

    List<Booking> get(@NotNull User user);
}
