package daos;

import business_logic.CinemaDatabase;
import business_logic.exceptions.DatabaseFailedException;
import domain.Cinema;
import domain.Hall;
import domain.Movie;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

public class ShowTimeDaoImpl implements ShowTimeDao {

    private static final HashMap<String, WeakReference<ShowTimeDao>> instances = new HashMap<>();
    private final String dbUrl;

    public static @NotNull ShowTimeDao getInstance(){
        return getInstance(CinemaDatabase.DB_URL);
    }

    public static @NotNull ShowTimeDao getInstance(@NotNull String dbUrl){
        ShowTimeDao inst = instances.get(dbUrl) != null ? instances.get(dbUrl).get() : null;
        if(inst != null)
            return inst;
        inst = new ShowTimeDaoImpl(dbUrl);
        instances.put(dbUrl, new WeakReference<>(inst));
        return inst;
    }

    private ShowTimeDaoImpl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    @Override
    public void insert(@NotNull ShowTime showTime) throws DatabaseFailedException {
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try(PreparedStatement s = conn.prepareStatement(
                    "INSERT OR IGNORE INTO ShowTimes(movie_id, hall_id, date) VALUES (?, ?, ?)"
            )) {
                s.setInt(1, showTime.getMovie().getId());
                s.setInt(2, showTime.getHall().getId());
                s.setString(3, showTime.getDate().toString());
                if(s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Database insertion failed.");
                try(PreparedStatement getId = conn.prepareStatement(
                        "SELECT last_insert_rowid() as showtime_id where (select last_insert_rowid()) > 0"
                )) {
                    try(ResultSet res = getId.executeQuery()){
                        if(!res.next())
                            throw new DatabaseFailedException("Database insertion failed.");
                        showTime.setId(res);
                    }
                }
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLiteException | NullPointerException e){
            if(e instanceof NullPointerException)
                throw new DatabaseFailedException("Null values are not allowed.");
            else if(((SQLiteException) e).getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code)
                throw new DatabaseFailedException("Database insertion failed: this showtime already exists.");
            else if (((SQLiteException) e).getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL.code)
                throw new DatabaseFailedException("Database insertion failed: movie, hall and date can not be null.");
            else if (((SQLiteException) e).getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY.code)
                throw new DatabaseFailedException("Database insertion failed: be sure that both the movie and hall have a valid ids.");
            else throw new RuntimeException(e);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(@NotNull ShowTime showTime, @NotNull ShowTime copy) throws DatabaseFailedException {
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try (PreparedStatement s = conn.prepareStatement(
                    "UPDATE ShowTimes SET movie_id = ?, hall_id = ?, date = ? WHERE showtime_id = ?"
            )) {
                s.setInt(1, copy.getMovie().getId());
                s.setInt(2, copy.getHall().getId());
                s.setString(3, copy.getDate().toString());
                s.setInt(4, showTime.getId());
                if(s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Update failed.");
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLiteException | NullPointerException e){
            if(e instanceof NullPointerException)
                throw new DatabaseFailedException("Null values are not allowed.");
            if(((SQLiteException) e).getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database update failed: this showtime already exists.");
            else if (((SQLiteException) e).getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY)
                throw new DatabaseFailedException("Database update failed: be sure that both the movie and hall have a valid ids.");
            else throw new RuntimeException(e);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(@NotNull ShowTime showTime) throws DatabaseFailedException {
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try (PreparedStatement s = conn.prepareStatement(
                    "DELETE FROM ShowTimes WHERE showtime_id = ?"
            )) {
                s.setInt(1, showTime.getId());
                if(s.executeUpdate() == 0){
                    throw new DatabaseFailedException("Deletion failed.");
                }
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ShowTime> get(@NotNull Movie movie, @NotNull Cinema cinema) {
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try(PreparedStatement s = conn.prepareStatement(
                    "SELECT showtime_id, movie_id, Halls.hall_id, cinema_id, date FROM ShowTimes JOIN Halls on ShowTimes.hall_id = Halls.hall_id WHERE movie_id = ? AND cinema_id = ?"
            )) {
                s.setInt(1, movie.getId());
                s.setInt(2, cinema.getId());
                try(ResultSet res = s.executeQuery()){
                    return getList(res, (showTimeList) -> {
                        Hall hall = new Hall(res);
                        ShowTime showTime = new ShowTime(res);
                        showTime.setMovie(movie);
                        showTime.setHall(hall);
                        showTime.setDate(LocalDateTime.parse(res.getString("date"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        return showTime;
                    });
                }
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public ShowTime get(ShowTime showTime) {
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try (PreparedStatement s = conn.prepareStatement(
                    "SELECT * FROM ShowTimes WHERE showtime_id = ?"
            )){
                s.setInt(1, showTime.getId());
                try(ResultSet res = s.executeQuery()){
                    if(res.next()){
                        showTime.setMovie(new Movie(res));
                        showTime.setHall(new Hall(res));
                        showTime.setDate(LocalDateTime.parse(res.getString(4), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        return showTime;
                    }
                    return null;
                }
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
