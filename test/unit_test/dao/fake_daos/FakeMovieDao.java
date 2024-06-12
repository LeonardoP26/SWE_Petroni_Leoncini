package unit_test.dao.fake_daos;

import daos.MovieDao;
import db.CinemaDatabaseTest;
import domain.Cinema;
import domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class FakeMovieDao implements MovieDao {

    @Override
    public void insert(@NotNull Movie movie) {
        CinemaDatabaseTest.runQuery(
                "SELECT MAX(movie_id) + 1 AS movie_id FROM movies",
                (res) -> {
                    if(res.next())
                        movie.setId(res);
                    return null;
                }
        );
    }

    @Override
    public void update(@NotNull Movie movie, @NotNull Movie copy) {

    }

    @Override
    public void delete(@NotNull Movie movie) {

    }

    @Override
    public List<Movie> get(@NotNull Cinema cinema) {
        return CinemaDatabaseTest.runQuery(
                "SELECT * FROM (ShowTimes JOIN Halls on ShowTimes.hall_id = Halls.hall_id) JOIN Movies on ShowTimes.movie_id = Movies.movie_id WHERE cinema_id = %d".formatted(cinema.getId()),
                (res) -> {
                    List<Movie> movies = new ArrayList<>();
                    while (res.next()) {
                        Movie m = new Movie(res);
                        m.setName(res.getString("movie_name"));
                        m.setDuration(Duration.of(res.getLong("duration"), ChronoUnit.MINUTES));
                    }
                    return movies;
                });
    }

    @Override
    public Movie get(Movie movie) {
        return CinemaDatabaseTest.runQuery(
                "SELECT * FROM Movies WHERE movie_id = %d".formatted(movie.getId()),
                (res) -> {
                    if (!res.next())
                        return null;
                    Movie m = new Movie(res);
                    m.setName(res.getString("movie_name"));
                    m.setDuration(Duration.of(res.getLong("duration"), ChronoUnit.MINUTES));
                    return m;
                });
    }
}
