package unit_test.repositories.fake_repositories;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.exceptions.NotEnoughFundsException;
import business_logic.repositories.BookingRepository;
import domain.Booking;
import domain.DatabaseEntity;
import domain.User;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public class FakeBookingRepository implements BookingRepository {

    @Override
    public void insert(@NotNull Booking booking, @NotNull User user) {

    }

    @Override
    public void update(@NotNull Booking oldBooking, @NotNull Booking newBooking, @NotNull User user) {

    }

    @Override
    public void delete(@NotNull Booking booking, @NotNull User user) {

    }

    @Override
    public List<Booking> get(@NotNull User user) {
        return List.of();
    }

    @Override
    public HashMap<Integer, WeakReference<Booking>> getEntities() {
        return null;
    }

    @Override
    public void update(@NotNull DatabaseEntity entity) {

    }
}
