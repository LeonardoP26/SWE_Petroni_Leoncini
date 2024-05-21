package daos;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Cinema;
import domain.Movie;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ShowTimeDao extends Dao {

    void insert(@NotNull ShowTime showTime) throws DatabaseFailedException;

    void update(@NotNull ShowTime showTime, @NotNull ShowTime copy) throws DatabaseFailedException;

    void delete(@NotNull ShowTime showTime) throws DatabaseFailedException;

    List<ShowTime> get(@NotNull Movie movie, @NotNull Cinema cinema);

    ShowTime get(ShowTime showTime);

}
