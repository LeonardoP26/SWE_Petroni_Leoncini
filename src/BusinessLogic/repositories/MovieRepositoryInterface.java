package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import Domain.Cinema;
import Domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface MovieRepositoryInterface {

    int insert(@NotNull Movie movie) throws DatabaseFailedException;

    boolean update(@NotNull Movie movie) throws DatabaseFailedException;

    boolean delete(@NotNull Movie movie);

    Movie get(int movieId);

    List<Movie> get(Cinema cinema);
}
