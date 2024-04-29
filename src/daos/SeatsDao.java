package daos;

import business_logic.exceptions.DatabaseFailedException;
import domain.Seat;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SeatsDao extends Dao {

    void insert(@NotNull Seat seat, int hallId) throws DatabaseFailedException;

    void update(@NotNull Seat seat, int hallId) throws DatabaseFailedException;

    void delete(@NotNull Seat seat) throws DatabaseFailedException;

    Seat get(int seatId);

    List<Seat> get(@NotNull ShowTime showTime);
}
