package BusinessLogic.repositories;

import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Cinema;
import Domain.Hall;
import Domain.Movie;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface CinemaRepositoryInterface {


    void insert(Cinema cinema) throws SQLException, UnableToOpenDatabaseException;

    List<Hall> getCinemaHalls(Cinema cinema) throws SQLException, UnableToOpenDatabaseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    List<Movie> getCinemaMovies(Cinema cinema) throws SQLException, UnableToOpenDatabaseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}
