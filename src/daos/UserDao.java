package daos;

import BusinessLogic.CinemaDatabase;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.User;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class UserDao implements UserDaoInterface{

    private static UserDaoInterface instance = null;

    public static UserDaoInterface getInstance(){
        if(instance == null)
            instance = new UserDao();
        return instance;
    }

    private UserDao() { }

    @Override
    public void insert(int id, String username, long balance) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR IGNORE INTO Users(id, username, balance) VALUES (?, ?, ?)"
        );
        s.setInt(1, id);
        s.setString(2, username);
        s.setLong(3, balance);
        s.executeUpdate();
    }

    @Override
    public boolean doesUsernameAlreadyExists(@NotNull String username) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT username FROM Users WHERE username = ?"
        );
        s.setString(1, username);
        ResultSet res = s.executeQuery();
        return res.isBeforeFirst();
    }

    @Override
    public int getNewId() throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        Statement s = conn.createStatement();
        ResultSet res = s.executeQuery("SELECT id FROM Users WHERE id = (SELECT MAX(id) FROM Users)");
        if(res != null)
            return res.getInt(1) + 1;
        return 0;

    }

    @Override
    public void update(User user) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "UPDATE Users SET username = ?, balance = ? WHERE id = ?"
        );
        s.setString(1, user.getUsername());
        s.setLong(2, user.getBalance());
        s.setInt(3, user.getId());
        s.executeUpdate();
    }

}
