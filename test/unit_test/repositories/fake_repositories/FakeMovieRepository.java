package unit_test.repositories.fake_repositories;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.repositories.MovieRepository;
import domain.Cinema;
import domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class FakeMovieRepository implements MovieRepository {

    @Override
    public void insert(@NotNull Movie movie) throws DatabaseFailedException {

    }

    @Override
    public void update(@NotNull Movie movie, @NotNull Consumer<Movie> edits) throws DatabaseFailedException, InvalidIdException {

    }

    @Override
    public void delete(@NotNull Movie movie) throws DatabaseFailedException, InvalidIdException {

    }

    @Override
    public List<Movie> get(@NotNull Cinema cinema) throws InvalidIdException {
        return List.of();
    }

    @Override
    public Movie get(Movie movie) throws InvalidIdException {
        return null;
    }

    @Override
    public HashMap<Integer, WeakReference<Movie>> getEntities() {
        return null;
    }
}
