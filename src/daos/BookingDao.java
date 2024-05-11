package daos;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Booking;
import domain.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface BookingDao extends Dao  {

    void insert(@NotNull Booking booking, @NotNull User user, @NotNull User copy) throws DatabaseFailedException, InvalidIdException;

    void update(@NotNull Booking oldBooking, @NotNull Booking newBooking, @NotNull User user, @NotNull User copy) throws DatabaseFailedException, InvalidIdException;

    void delete(@NotNull Booking booking, @NotNull User user, boolean commit) throws DatabaseFailedException, InvalidIdException;

    List<Booking> get(@NotNull User user) throws InvalidIdException;
}
