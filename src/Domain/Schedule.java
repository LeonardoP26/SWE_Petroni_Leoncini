package Domain;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class Schedule {

    public Schedule(Movie movie, Hall hall, LocalDateTime date){
        this.movie = movie;
        this.hall = hall;
        this.date = date;
    }

    private Movie movie;
    private LocalDateTime date;
    private Hall hall;


    public Movie getMovie() {
        return movie;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Hall getHall() {
        return hall;
    }

}
