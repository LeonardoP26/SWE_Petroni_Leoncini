package business_logic.repositories;

import business_logic.exceptions.DatabaseFailedException;
import domain.Movie;
import domain.Seat;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SeatsRepositoryInterface {

    void insert(Seat seat, int hallId) throws DatabaseFailedException;

    void update(@NotNull Seat seat, int hallId) throws DatabaseFailedException;

    void delete(@NotNull Seat seat) throws DatabaseFailedException;

    Movie get(int seatId);

    List<Seat> get(@NotNull ShowTime showTime);
}
