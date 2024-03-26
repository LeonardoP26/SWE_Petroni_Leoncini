package Domain;

import BusinessLogic.UnableToOpenDatabaseException;
import BusinessLogic.repositories.CinemaRepository;
import BusinessLogic.repositories.CinemaRepositoryInterface;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Cinema {

    private final int id;
    private final String name;

    private final CinemaRepositoryInterface cinemaRepo = CinemaRepository.getInstance();

    public Cinema(int id, String name){
        this.id = id;
        this.name = name;
    }

    public List<Hall> getCinemaHalls() throws SQLException, UnableToOpenDatabaseException {
        return cinemaRepo.getCinemaHalls(this);
    }

    public List<Movie> getCinemaMovies() throws SQLException, UnableToOpenDatabaseException {
        List<Movie> movies = new ArrayList<>();
        for(Hall hall : getCinemaHalls()){
            for(ShowTime showTime : hall.getShowTimes()) {
                if (movies.stream().anyMatch(movie -> movie.getId() == showTime.getMovieId()))
                    continue;
                movies.add(showTime.getMovie());
            }
        }
        return movies;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
