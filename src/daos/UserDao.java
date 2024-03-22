package daos;

import BusinessLogic.CinemaDatabase;
import Domain.User;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDao implements UserDaoInterface{

    @Override
    public void insert(@NotNull User user) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR IGNORE INTO Users(id, username, balance) VALUES (?, ?, ?)"
        );
        s.setInt(1, user.getId());
        s.setString(2, user.getUsername());
        s.setLong(3, user.getBalance());
        s.executeUpdate();
    }

}
