package domain;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

public class Movie implements DatabaseEntity {

    private int id = ENTITY_WITHOUT_ID;
    private String name;
    private Duration duration;

    public Movie(String name, Duration duration){
        this.name = name;
        this.duration = duration;
    }

    public Movie(@NotNull ResultSet res) throws SQLException {
        id = res.getInt("movie_id");
//        name = res.getString("movie_name");
//        duration = Duration.of(res.getLong("duration"), ChronoUnit.MINUTES);
    }

    public Movie(@NotNull Movie movie){
        this.name = movie.name;
        this.duration = movie.duration;
    }

    @Override
    public String getName() {
        return name;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(@NotNull ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("movie_id");
    }

    public void copy(@NotNull Movie movie){
        this.name = movie.name;
        this.duration = movie.duration;
    }

    @Override
    public void resetId() {
        this.id = ENTITY_WITHOUT_ID;
    }
}
