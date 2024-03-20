package Domain;

import java.time.Duration;

public class Movie {

    public Movie(String name, Duration duration){
        this.name = name;
        this.duration = duration;
    }

    private String name;
    private Duration duration;

    public String getName() {
        return name;
    }

    public Duration getDuration() {
        return duration;
    }
}
