package daos;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Hall;
import domain.Seat;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SeatDao extends Dao {

    void insert(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException;

    void update(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException;

    void delete(@NotNull Seat seat) throws DatabaseFailedException, InvalidIdException;

    Seat get(int seatId) throws InvalidIdException;

    List<Seat> get(@NotNull ShowTime showTime) throws InvalidIdException;
}
