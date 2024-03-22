package daos;

import BusinessLogic.CinemaDatabase;
import Domain.Cinema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CinemaDao implements CinemaDaoInterface{

    @Override
    public void insert(Cinema cinema) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR IGNORE INTO Cinemas(id) VALUES (?)"
        );
        s.setInt(1, cinema.getId());
        s.executeUpdate();
    }

}
