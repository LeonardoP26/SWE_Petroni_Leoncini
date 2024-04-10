package Domain;

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
        try{
            id = res.getInt("Movies.id");
        } catch (SQLException e){
            id = res.getInt("id");
        }
        try{
            name = res.getString("Movies.name");
        } catch (SQLException e){
            name = res.getString("name");
        }
        try{
            duration = Duration.of(res.getLong("Movies.duration"), ChronoUnit.MINUTES);
        } catch (SQLException e){
            duration = Duration.of(res.getLong("duration"), ChronoUnit.MINUTES);
        }
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
}
