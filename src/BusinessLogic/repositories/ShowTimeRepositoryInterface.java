package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import Domain.Movie;
import Domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface ShowTimeRepositoryInterface {
    int insert(@NotNull ShowTime showTime) throws DatabaseFailedException;

    boolean update(@NotNull ShowTime showTime) throws DatabaseFailedException;

    boolean delete(@NotNull ShowTime showTime);

    ShowTime get(int showTimeId);

    List<ShowTime> get(@NotNull Movie movie);

}
