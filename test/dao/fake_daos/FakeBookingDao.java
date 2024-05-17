package dao.fake_daos;

import business_logic.exceptions.DatabaseFailedException;
import daos.BookingDao;
import db.CinemaDatabaseTest;
import domain.Booking;
import domain.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FakeBookingDao implements BookingDao {

    @Override
    public void insert(@NotNull Booking booking, @NotNull User user, @NotNull User copy) {

    }

    @Override
    public void update(@NotNull Booking oldBooking, @NotNull Booking newBooking, @NotNull User user, @NotNull User copy) {

    }

    @Override
    public void delete(@NotNull Booking booking, @NotNull User user) {

    }

    @Override
    public List<Booking> get(@NotNull User user) {
        if(user == CinemaDatabaseTest.getTestUser1())
            return List.of(CinemaDatabaseTest.getTestBooking1());
        if(user == CinemaDatabaseTest.getTestUser2())
            return List.of(CinemaDatabaseTest.getTestBooking2());
        return List.of();
    }
}
