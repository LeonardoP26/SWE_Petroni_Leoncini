package Domain;

import java.time.Duration;

public class Movie {

    public Movie(int id, String name, Duration duration){
        this.id = id;
        this.name = name;
        this.duration = duration;
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

    public int getId() {
        return id;
    }
}
