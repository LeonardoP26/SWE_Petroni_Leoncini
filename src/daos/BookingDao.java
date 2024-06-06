package daos;

import business_logic.exceptions.DatabaseFailedException;
import domain.Booking;
import domain.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface BookingDao extends Dao  {

    void insert(@NotNull Booking booking, @NotNull User user, @NotNull User copy) throws DatabaseFailedException;

    void update(@NotNull Booking oldBooking, @NotNull Booking newBooking, @NotNull User user, @NotNull User copy) throws DatabaseFailedException;

    void delete(@NotNull Booking booking, @NotNull User user) throws DatabaseFailedException;

    List<Booking> get(@NotNull User user);
}
