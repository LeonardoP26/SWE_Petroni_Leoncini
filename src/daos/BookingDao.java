package daos;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Booking;
import domain.Seat;
import domain.ShowTime;
import domain.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface BookingDao extends Dao  {

    void insert(@NotNull Booking booking, User user) throws DatabaseFailedException, InvalidIdException;

    void delete(@NotNull Booking booking) throws DatabaseFailedException, InvalidIdException;

    List<Booking> get(@NotNull User user) throws InvalidIdException;
}
