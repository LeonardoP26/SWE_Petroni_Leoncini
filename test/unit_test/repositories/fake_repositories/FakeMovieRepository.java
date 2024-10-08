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
    public void insert(@NotNull Movie movie) {

    }

    @Override
    public void update(@NotNull Movie movie, @NotNull Consumer<Movie> edits) {

    }

    @Override
    public void delete(@NotNull Movie movie) {

    }

    @Override
    public List<Movie> get(@NotNull Cinema cinema) {
        return List.of();
    }

    @Override
    public Movie get(Movie movie) {
        return null;
    }

    @Override
    public HashMap<Integer, WeakReference<Movie>> getEntities() {
        return null;
    }
}
