package daos;

import business_logic.exceptions.DatabaseFailedException;
import domain.Booking;
import domain.Seat;
import domain.ShowTime;
import domain.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface BookingDao extends Dao  {

    void insert(@NotNull ShowTime showTime, List<Seat> seats, User user) throws DatabaseFailedException;

    void delete(@NotNull Booking booking) throws DatabaseFailedException;

    List<Booking> get(@NotNull User user);
}
