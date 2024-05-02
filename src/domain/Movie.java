package domain;

import business_logic.exceptions.InvalidIdException;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class Movie implements DatabaseEntity {

    public Movie(String name, Duration duration){
        this.name = name;
        this.duration = duration;
    }

    public Movie(@NotNull ResultSet res) throws SQLException {
        id = res.getInt("movie_id");
        name = res.getString("movie_name");
        duration = Duration.of(res.getLong("duration"), ChronoUnit.MINUTES);
    }

    private int id = ENTITY_WITHOUT_ID;
    private String name;
    private Duration duration;

    @Override
    public String getName() {
        return name;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId(@NotNull ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("movie_id");
    }
}
