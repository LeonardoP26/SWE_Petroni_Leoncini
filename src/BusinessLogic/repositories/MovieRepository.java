package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseInsertionFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Cinema;
import Domain.Movie;
import daos.MovieDao;
import daos.MovieDaoInterface;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MovieRepository extends Repository implements MovieRepositoryInterface{

    private final MovieDaoInterface dao = MovieDao.getInstance();

    private static MovieRepositoryInterface instance = null;

    public static MovieRepositoryInterface getInstance() {
        if(instance == null)
            instance = new MovieRepository();
        return instance;
    }

    private MovieRepository() { }


    @Override
    public int insert(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException {
        try(ResultSet res = dao.insert(movie.getName(), movie.getDuration())){
            if(res.next())
                return res.getInt(1);
            throw new DatabaseInsertionFailedException("Database insertion Failed");
        }
    }

    @Override
    public boolean update(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException {
        return dao.update(movie.getId(), movie.getName(), movie.getDuration());
    }

    @Override
    public boolean delete(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException {
        return dao.delete(movie.getId());
    }

    @Override
    public Movie get(int movieId) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.get(movieId)){
            if(res.next())
                return new Movie(res);
            return null;
        }
    }

    @Override
    public List<Movie> get(Cinema cinema) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.get(cinema)){
            return getList(res, () -> new Movie(res));
        }
    }


}
