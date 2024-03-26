package daos;

import BusinessLogic.CinemaDatabase;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MovieDao implements MovieDaoInterface{

    private static MovieDaoInterface instance = null;

    public static MovieDaoInterface getInstance(){
        if(instance == null)
            instance = new MovieDao();
        return instance;
    }

    private MovieDao() { }

    @Override
    public void insert(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR IGNORE INTO Movies(id, name, duration) VALUES (?, ?, ?)"
        );
        s.setInt(1, movie.getId());
        s.setString(2, movie.getName());
        s.setLong(3, movie.getDuration().toSeconds());
        s.executeUpdate();
    }

    @Override
    public ResultSet getMovie(int movieId) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM Movies WHERE id = ?"
        );
        s.setInt(1, movieId);
        return s.executeQuery();
    }

}
