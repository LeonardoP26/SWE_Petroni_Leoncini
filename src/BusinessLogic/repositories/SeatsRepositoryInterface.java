package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import Domain.Movie;
import Domain.Seat;
import Domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface SeatsRepositoryInterface {

    int insert(Seat seat, int hallId) throws DatabaseFailedException;

    boolean update(@NotNull Seat seat, int hallId) throws DatabaseFailedException;

    boolean delete(@NotNull Seat seat);

    Movie get(int seatId);

    List<Seat> get(@NotNull ShowTime showTime);
}
