package domain;

import business_logic.exceptions.InvalidIdException;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Cinema implements DatabaseEntity {

    private int id = ENTITY_WITHOUT_ID;
    private String name;

    public Cinema (@NotNull ResultSet res) throws SQLException {
        id = res.getInt("cinema_id");
        name = res.getString("cinema_name");
    }

    public Cinema(String name){
        this.name = name;
    }

    private ArrayList<Hall> halls = new ArrayList<>();
    private ArrayList<Movie> movies = new ArrayList<>();


    public ArrayList<Hall> getHalls() {
        return halls;
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public void setMovies(@NotNull ArrayList<Movie> movies){
        this.movies = movies;
    }

    public void setHalls(@NotNull ArrayList<Hall> halls) {
        this.halls = halls;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId(@NotNull ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("cinema_id");
    }

}
