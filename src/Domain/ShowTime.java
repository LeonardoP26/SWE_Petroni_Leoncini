package Domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ShowTime implements DatabaseEntity {

    public ShowTime(ResultSet res) throws SQLException {
        this.id = res.getInt("showtime_id");
    }

    public ShowTime(Movie movie, Hall hall, LocalDateTime date){
        this.movie = movie;
        this.hall = hall;
        this.date = date;
    }

    private int id = ENTITY_WITHOUT_ID;
    private Movie movie = null;
    private Hall hall = null;
    private LocalDateTime date = null;
    private Cinema cinema = null;


    public int getId() {
        return id;
    }

    @Override
    public String getName(){
        return "Hall " + hall.getName() + " - " + date.getDayOfMonth() + " " + date.getMonth() + " " + date.getYear() + " at " + date.getHour() + ":" + date.getMinute() + " - " + hall.getHallType();
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) { this.date = date; }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) { this.hall = hall; }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) { this.movie = movie; }

    public void setId(int id) {
        this.id = id;
    }

    public void setCinema(Cinema cinema){
        this.cinema = cinema;
    }

    public Cinema getCinema() {
        return cinema;
    }
}
