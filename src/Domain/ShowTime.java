package Domain;

import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import BusinessLogic.repositories.HallRepository;
import BusinessLogic.repositories.HallRepositoryInterface;
import BusinessLogic.repositories.MovieRepository;
import BusinessLogic.repositories.MovieRepositoryInterface;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ShowTime implements DatabaseEntity {

    public ShowTime(ResultSet res) throws SQLException {
        try{
            this.id = res.getInt("ShowTimes.id");
        } catch (SQLException e){
            this.id = res.getInt("id");
        }
    }

    public ShowTime(Movie movie, Hall hall, LocalDateTime date){
        this.movie = movie;
        this.hall = hall;
        this.date = date;
    }


    private Movie movie;
    private Hall hall;
    private int id = ENTITY_WITHOUT_ID;
    private LocalDateTime date;


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
}
