package daos;

import business_logic.exceptions.DatabaseFailedException;
import domain.Hall;
import domain.Seat;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SeatDao extends Dao {

    void insert(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException;

    void update(@NotNull Seat seat, @NotNull Seat copy, @NotNull Hall hall) throws DatabaseFailedException;

    void delete(@NotNull Seat seat) throws DatabaseFailedException;

    List<Seat> get(@NotNull ShowTime showTime);

    Seat get(Seat seat);
}
