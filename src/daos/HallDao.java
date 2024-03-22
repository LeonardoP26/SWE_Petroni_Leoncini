package daos;

import BusinessLogic.CinemaDatabase;
import Domain.Hall;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HallDao implements HallDaoInterface{

    @Override
    public void insert(@NotNull Hall hall) throws SQLException {
        Connection con = CinemaDatabase.getConnection();
        PreparedStatement s = con.prepareStatement(
                "INSERT OR IGNORE INTO Halls(id, cinemaId) VALUES (?, ?)"
        );
        s.setInt(1, hall.getId());
        s.setInt(2, hall.getCinema().getId());
        s.executeUpdate();
    }

}
