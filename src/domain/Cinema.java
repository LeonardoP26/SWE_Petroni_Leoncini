package domain;

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

    private List<Hall> halls = new ArrayList<>();
    private List<Movie> movies = new ArrayList<>();


    public List<Hall> getHalls() {
        return halls;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(@NotNull List<Movie> movies){
        this.movies = movies;
    }

    public void setHalls(List<Hall> halls) {
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
