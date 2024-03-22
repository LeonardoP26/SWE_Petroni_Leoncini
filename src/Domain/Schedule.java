package Domain;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public class Schedule {

    public Schedule(int id, Movie movie, Hall hall, String date){
        this(id, movie, hall, LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public Schedule(int id, Movie movie, Hall hall, LocalDateTime date){
        this.id = id;
        this.movie = movie;
        this.hall = hall;
        this.date = date.truncatedTo(ChronoUnit.MINUTES);
    }

    private int id;
    private Movie movie;
    private LocalDateTime date;
    private Hall hall;


    public int getId() {
        return id;
    }

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
