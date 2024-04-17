package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import Domain.Cinema;
import Domain.Movie;
import daos.MovieDao;
import daos.MovieDaoInterface;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

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
    public int insert(@NotNull Movie movie) throws DatabaseFailedException {
        try(ResultSet res = dao.insert(movie.getName(), movie.getDuration())){
            if(res.next())
                return res.getInt(1);
            throw new DatabaseFailedException("Database insertion failed.");
        } catch (SQLiteException e){
            if(e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database insertion failed: this movie already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database insertion failed: ensure movie id, name and duration are not null.");
            else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(@NotNull Movie movie) throws DatabaseFailedException {
        try{
            return dao.update(movie.getId(), movie.getName(), movie.getDuration());
        } catch (SQLiteException e){
            if(e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database update failed: this movie already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database update failed: ensure movie id, name and duration are not null.");
            else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(@NotNull Movie movie) {
        try{
            return dao.delete(movie.getId());
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Movie get(int movieId) {
        try(ResultSet res = dao.get(movieId)){
            if(res.next())
                return new Movie(res);
            return null;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Movie> get(Cinema cinema) {
        try(ResultSet res = dao.get(cinema)){
            return getList(res, (movieList) -> new Movie(res));
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


}
