package daos;

import BusinessLogic.CinemaDatabase;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Cinema;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

public class MovieDao implements MovieDaoInterface{

    private static MovieDaoInterface instance = null;

    public static MovieDaoInterface getInstance(){
        if(instance == null)
            instance = new MovieDao();
        return instance;
    }

    private MovieDao() { }

    @Override
    public ResultSet insert(String movieName, Duration movieDuration) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR IGNORE INTO Movies(movie_id, movie_name, duration) VALUES (null, ?, ?)"
        );
        s.setString(1, movieName);
        s.setLong(2, movieDuration.toMinutes());
        s.executeUpdate();
        PreparedStatement getId = conn.prepareStatement("SELECT last_insert_rowid()");
        return getId.executeQuery();
    }

    @Override
    public boolean update(int movieId, String movieName, Duration movieDuration) throws SQLException, UnableToOpenDatabaseException {
        try(PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "UPDATE Movies SET movie_name = ?, duration = ? WHERE movie_id = ?"
        )){
            s.setString(1, movieName);
            s.setLong(2, movieDuration.toMinutes());
            s.setInt(3, movieId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int movieId) throws SQLException, UnableToOpenDatabaseException {
        try (PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "DELETE FROM Movies WHERE movie_id = ?"
        )) {
            s.setInt(1, movieId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public ResultSet get(int movieId) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM Movies WHERE movie_id = ?"
        );
        s.setInt(1, movieId);
        return s.executeQuery();
    }

    @Override
    public ResultSet get(@NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT DISTINCT Movies.movie_id, movie_name, duration FROM (ShowTimes JOIN Movies ON ShowTimes.movie_id = Movies.movie_id) JOIN Halls ON ShowTimes.hall_id = Halls.hall_id WHERE cinema_id = ?"
        );
        s.setInt(1, cinema.getId());
        return s.executeQuery();
    }

}
