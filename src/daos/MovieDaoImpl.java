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

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class MovieDaoImpl implements MovieDao {

    private static final HashMap<String, WeakReference<MovieDao>> instances = new HashMap<>();
    private final String dbUrl;

    public static @NotNull MovieDao getInstance(){
        return getInstance(CinemaDatabase.DB_URL);
    }

    public static @NotNull MovieDao getInstance(@NotNull String dbUrl){
        MovieDao inst = instances.get(dbUrl) != null ? instances.get(dbUrl).get() : null;
        if(inst != null)
            return inst;
        inst = new MovieDaoImpl(dbUrl);
        instances.put(dbUrl, new WeakReference<>(inst));
        return inst;
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
    public void update(@NotNull Movie movie, @NotNull Movie copy) throws DatabaseFailedException {
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try (PreparedStatement s = conn.prepareStatement(
                    "UPDATE Movies SET movie_name = ?, duration = ? WHERE movie_id = ?"
            )) {
                s.setString(1, copy.getName());
                s.setLong(2, copy.getDuration().toMinutes());
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
    public void delete(@NotNull Movie movie) throws DatabaseFailedException {
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try (PreparedStatement s = conn.prepareStatement(
                    "DELETE FROM Movies WHERE movie_id = ?"
            )) {
                s.setInt(1, movie.getId());
                if (s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Deletion failed.");
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Movie> get(@NotNull Cinema cinema) {
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
