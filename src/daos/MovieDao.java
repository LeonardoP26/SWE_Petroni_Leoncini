package daos;

import BusinessLogic.CinemaDatabase;
import Domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MovieDao implements MovieDaoInterface{

    @Override
    public void insert(@NotNull Movie movie) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR IGNORE INTO Movies(id, name, duration) VALUES (?, ?, ?)"
        );
        s.setInt(1, movie.getId());
        s.setString(2, movie.getName());
        s.setLong(3, movie.getDuration().toSeconds());
        s.executeUpdate();
    }

}
