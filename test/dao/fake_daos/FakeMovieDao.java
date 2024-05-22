package dao.fake_daos;

import business_logic.exceptions.DatabaseFailedException;
import daos.MovieDao;
import domain.Cinema;
import domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FakeMovieDao implements MovieDao {

    @Override
    public void insert(@NotNull Movie movie) {

    }

    @Override
    public void update(@NotNull Movie movie, @NotNull Movie copy) {

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
}
