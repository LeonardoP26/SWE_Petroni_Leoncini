package daos;

import business_logic.exceptions.DatabaseFailedException;
import domain.Cinema;
import domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MovieDao extends Dao {

    void insert(@NotNull Movie movie) throws DatabaseFailedException;

    void update(@NotNull Movie movie) throws DatabaseFailedException;

    void delete(@NotNull Movie movie) throws DatabaseFailedException;

    Movie get(int movieId);

    List<Movie> get(@NotNull Cinema cinema);
}
