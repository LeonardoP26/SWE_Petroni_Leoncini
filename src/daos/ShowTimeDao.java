package daos;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Movie;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ShowTimeDao extends Dao {

    void insert(@NotNull ShowTime showTime) throws DatabaseFailedException, InvalidIdException;

    void update(@NotNull ShowTime showTime) throws DatabaseFailedException, InvalidIdException;

    void delete(@NotNull ShowTime showTime) throws DatabaseFailedException, InvalidIdException;

    ShowTime get(int showTimeId) throws InvalidIdException;

    List<ShowTime> get(@NotNull Movie movie) throws InvalidIdException;

}
