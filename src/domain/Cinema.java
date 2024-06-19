package domain;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Cinema implements DatabaseEntity {

    private int id = ENTITY_WITHOUT_ID;
    private String name;
    private ArrayList<Movie> movies = new ArrayList<>();

    public Cinema (@NotNull ResultSet res) throws SQLException {
        id = res.getInt("cinema_id");
    }

    public Cinema(String name){
        this.name = name;
    }

    public Cinema(@NotNull Cinema cinema) {
        this.name = cinema.getName();
        this.movies = cinema.getMovies();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public void setId(@NotNull ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("cinema_id");
    }

    @Override
    public void resetId() {
        this.id = ENTITY_WITHOUT_ID;
    }

    public void copy(@NotNull Cinema cinema) {
        this.name = cinema.getName();
        this.movies = cinema.getMovies();
    }
}
