package daos;

import business_logic.CinemaDatabase;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Cinema;
import domain.DatabaseEntity;
import domain.Movie;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class MovieDaoImpl implements MovieDao {

    private static final HashMap<String, MovieDao> instances = new HashMap<>();
    private final String dbUrl;

    public static MovieDao getInstance(){
        return getInstance(CinemaDatabase.DB_URL);
    }

    public static MovieDao getInstance(String dbUrl){
        if(instances.containsKey(dbUrl))
            return instances.get(dbUrl);
        MovieDao newInstance = new MovieDaoImpl(dbUrl);
        instances.put(dbUrl, newInstance);
        return newInstance;
    }

    private MovieDaoImpl(String dbUrl){
        this.dbUrl = dbUrl;
    }

    @Override
    public void insert(@NotNull Movie movie) throws DatabaseFailedException {
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try (PreparedStatement s = conn.prepareStatement(
                    "INSERT OR IGNORE INTO Movies(movie_id, movie_name, duration) VALUES (null, ?, ?)"
            )) {
                s.setString(1, movie.getName());
                s.setLong(2, movie.getDuration().toMinutes());
                if (s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Database insertion failed.");
                try (PreparedStatement getId = conn.prepareStatement(
                        "SELECT last_insert_rowid() as movie_id where (select last_insert_rowid()) > 0"
                )) {
                    try(ResultSet res = getId.executeQuery()){
                        if(!res.next())
                            throw new DatabaseFailedException("Database insertion failed.");
                        movie.setId(res);
                    }
                }
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
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
    public void update(@NotNull Movie movie) throws DatabaseFailedException, InvalidIdException {
        if(movie.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This movie is not in the database.");
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try (PreparedStatement s = conn.prepareStatement(
                    "UPDATE Movies SET movie_name = ?, duration = ? WHERE movie_id = ?"
            )) {
                s.setString(1, movie.getName());
                s.setLong(2, movie.getDuration().toMinutes());
                s.setInt(3, movie.getId());
                if(s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Query result is empty.");
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
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
    public void delete(@NotNull Movie movie) throws DatabaseFailedException, InvalidIdException {
        if(movie.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This movie is not in the database.");
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try (PreparedStatement s = conn.prepareStatement(
                    "DELETE FROM Movies WHERE movie_id = ?"
            )) {
                s.setInt(1, movie.getId());
                if (s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Deletion failed.");
            }
            try(PreparedStatement s = conn.prepareStatement(
                    "SELECT -1 AS movie_id"
            )){
                movie.setId(s.executeQuery());
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Movie get(int movieId) throws InvalidIdException {
        if(movieId < 1)
            throw new InvalidIdException("Id not valid");
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try(PreparedStatement s = conn.prepareStatement(
                    "SELECT * FROM Movies WHERE movie_id = ?"
            )) {
                s.setInt(1, movieId);
                try(ResultSet res = s.executeQuery()){
                    if(res.next())
                        return new Movie(res);
                    return null;
                }
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Movie> get(@NotNull Cinema cinema) throws InvalidIdException {
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try(PreparedStatement s = conn.prepareStatement(
                    "SELECT DISTINCT Movies.movie_id, movie_name, duration FROM (ShowTimes JOIN Movies ON ShowTimes.movie_id = Movies.movie_id) JOIN Halls ON ShowTimes.hall_id = Halls.hall_id WHERE cinema_id = ?"
            )) {
                s.setInt(1, cinema.getId());
                try(ResultSet res = s.executeQuery()){
                    return getList(res, (movieList) -> new Movie(res));
                }
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
