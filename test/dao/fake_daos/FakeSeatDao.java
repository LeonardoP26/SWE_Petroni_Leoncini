package dao.fake_daos;

import business_logic.exceptions.DatabaseFailedException;
import daos.SeatDao;
import domain.Hall;
import domain.Seat;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FakeSeatDao implements SeatDao {

    @Override
    public void insert(@NotNull Seat seat, @NotNull Hall hall) {

    }

    @Override
    public void update(@NotNull Seat seat, @NotNull Seat copy, @NotNull Hall hall) {

    }

    @Override
    public void delete(@NotNull Seat seat) {

    }

    @Override
    public List<Seat> get(@NotNull ShowTime showTime) {
        return showTime.getHall().getSeats();
    }

    @Override
    public Seat get(Seat seat) {
        return seat;
    }
}
