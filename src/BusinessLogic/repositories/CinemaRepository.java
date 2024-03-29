package BusinessLogic.repositories;

import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Cinema;
import Domain.Hall;
import Domain.Movie;
import daos.CinemaDao;
import daos.CinemaDaoInterface;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CinemaRepository extends Repository implements CinemaRepositoryInterface {

    private final CinemaDaoInterface dao = CinemaDao.getInstance();

    private static CinemaRepositoryInterface instance = null;
    public static CinemaRepositoryInterface getInstance(){
        if(instance == null)
            instance = new CinemaRepository();
        return instance;
    }

    private CinemaRepository() { }

    @Override
    public void insert(Cinema cinema) throws SQLException, UnableToOpenDatabaseException {
        dao.insert(cinema);
    }

    @Override
    public List<Hall> getCinemaHalls(Cinema cinema) throws SQLException, UnableToOpenDatabaseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        try(ResultSet res = dao.getCinemaHalls(cinema)){
            return getList(res, Hall.class);
        }
    }

    @Override
    public List<Movie> getCinemaMovies(Cinema cinema) throws SQLException, UnableToOpenDatabaseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        try(ResultSet res = dao.getCinemaMovies(cinema)){
            return getList(res, Movie.class);
        }
    }


}
