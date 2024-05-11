package business_logic.repositories;

import business_logic.Observer;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.exceptions.NotEnoughFundsException;
import domain.Booking;
import domain.DatabaseEntity;
import domain.User;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public interface BookingRepository extends Observer<DatabaseEntity> {

    void insert(@NotNull Booking booking, @NotNull User user) throws DatabaseFailedException, InvalidIdException, NotEnoughFundsException;

    void update(@NotNull Booking oldBooking, @NotNull Booking newBooking, @NotNull User user) throws NotEnoughFundsException, DatabaseFailedException, InvalidIdException;

    void delete(@NotNull Booking booking, @NotNull User user) throws DatabaseFailedException, InvalidIdException;

    List<Booking> get(@NotNull User user) throws InvalidIdException;

    HashMap<Integer, WeakReference<Booking>> getEntities();
}
