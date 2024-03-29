package Domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class Movie implements DatabaseEntity {

    public Movie(int id, String name, Duration duration){
        this.id = id;
        this.name = name;
        this.duration = duration;
    }

    public Movie(ResultSet res) throws SQLException {
        this(res.getInt(1), res.getString(2), Duration.of(res.getLong(3), ChronoUnit.SECONDS));
    }

    private final int id;
    private final String name;
    private final Duration duration;

    public String getName() {
        return name;
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public int getId() {
        return id;
    }
}
