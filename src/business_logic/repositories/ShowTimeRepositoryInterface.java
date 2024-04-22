package business_logic.repositories;

import business_logic.exceptions.DatabaseFailedException;
import domain.Movie;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ShowTimeRepositoryInterface {

    void insert(@NotNull ShowTime showTime) throws DatabaseFailedException;

    void update(@NotNull ShowTime showTime) throws DatabaseFailedException;

    void delete(@NotNull ShowTime showTime) throws DatabaseFailedException;

    ShowTime get(int showTimeId);

    List<ShowTime> get(@NotNull Movie movie);

}
