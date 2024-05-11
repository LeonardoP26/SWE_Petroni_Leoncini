package daos;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Cinema;
import domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MovieDao extends Dao {

    void insert(@NotNull Movie movie) throws DatabaseFailedException;

    void update(@NotNull Movie movie, @NotNull Movie copy) throws DatabaseFailedException, InvalidIdException;

    void delete(@NotNull Movie movie) throws DatabaseFailedException, InvalidIdException;

    List<Movie> get(@NotNull Cinema cinema) throws InvalidIdException;
}
