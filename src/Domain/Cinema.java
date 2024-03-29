package Domain;

import BusinessLogic.UnableToOpenDatabaseException;
import BusinessLogic.repositories.CinemaRepository;
import BusinessLogic.repositories.CinemaRepositoryInterface;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Cinema implements DatabaseEntity {

    private final int id;
    private final String name;

    private final CinemaRepositoryInterface cinemaRepo = CinemaRepository.getInstance();

    public Cinema(int id, String name){
        this.id = id;
        this.name = name;
    }

    public List<Hall> getCinemaHalls() throws SQLException, UnableToOpenDatabaseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return cinemaRepo.getCinemaHalls(this);
    }

    public List<Movie> getCinemaMovies() throws SQLException, UnableToOpenDatabaseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return cinemaRepo.getCinemaMovies(this);
    }


    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
