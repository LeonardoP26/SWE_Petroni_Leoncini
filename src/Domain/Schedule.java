package Domain;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class Schedule {
    public Movie getMovieName() {
        return movieName;
    }

    public void setMovieName(Movie movieName) {
        this.movieName = movieName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    private Movie movieName;
    private LocalDateTime date;
    private UUID id;

    public Schedule(Movie movieName, LocalDateTime date, UUID id) {
        this.movieName = movieName;
        this.date = date;
        this.id = id;
    }
}
