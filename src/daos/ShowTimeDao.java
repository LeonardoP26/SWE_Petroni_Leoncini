package daos;

import business_logic.CinemaDatabase;
import domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.LocalDateTime;

public class ShowTimeDao implements ShowTimeDaoInterface {

    private static ShowTimeDaoInterface instance = null;

    public static ShowTimeDaoInterface getInstance(){
        if(instance == null)
            instance = new ShowTimeDao();
        return instance;
    }

    private ShowTimeDao() { }

    @Override
    public ResultSet insert(int movieId, int hallId, LocalDateTime date) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR IGNORE INTO ShowTimes(movie_id, hall_id, date) VALUES (?, ?, ?)"
        );
        s.setInt(1, movieId);
        s.setInt(2, hallId);
        s.setString(3, date.toString());
        s.executeUpdate();
        PreparedStatement getId = conn.prepareStatement(
                "SELECT last_insert_rowid() as showtime_id where (select last_insert_rowid()) > 0"
        );
        return getId.executeQuery();
    }

    @Override
    public boolean update(int showTimeId, int movieId, int hallId) throws SQLException {
        try (PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "UPDATE ShowTimes SET movie_id = ?, hall_id = ? WHERE showtime_id = ?"
        )) {
            s.setInt(1, movieId);
            s.setInt(2, hallId);
            s.setInt(3, showTimeId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int showTimeId) throws SQLException {
        try (PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "DELETE FROM ShowTimes WHERE showtime_id = ?"
        )) {
            s.setInt(1, showTimeId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public ResultSet get(int showTimeId) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM ShowTimes WHERE showtime_id = ?"
        );
        s.setInt(1, showTimeId);
        return s.executeQuery();
    }

    @Override
    public ResultSet get(@NotNull Movie movie) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM ShowTimes JOIN Halls on ShowTimes.hall_id = Halls.hall_id WHERE movie_id = ?"
        );
        s.setInt(1, movie.getId());
        return s.executeQuery();
    }

}
