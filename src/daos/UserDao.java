package daos;

import BusinessLogic.CinemaDatabase;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
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
    public ResultSet insert(String username, String password, long balance) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT INTO Users(username, password, balance) VALUES (?, ?, ?)"
        );
        s.setString(1, username);
        s.setString(2, password);
        s.setLong(3, balance);
        s.executeUpdate();
        PreparedStatement getId = conn.prepareStatement("SELECT last_insert_rowid()");
        return getId.executeQuery();
    }

    @Override
    public boolean update(int userId, String username, String password, long balance) throws SQLException, UnableToOpenDatabaseException {
        try (PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "UPDATE Users SET username = ?, password = ?, balance = ? WHERE user_id = ?"
        )) {
            s.setString(1, username);
            s.setString(2, password);
            s.setLong(3, balance);
            s.setInt(4, userId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int userId) throws SQLException, UnableToOpenDatabaseException {
        try (PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "DELETE FROM Users WHERE user_id = ?"
        )) {
            s.setInt(1, userId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public ResultSet get(int userId) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM Users WHERE user_id = ?"
        );
        s.setInt(1, userId);
        return s.executeQuery();
    }

    @Override
    public ResultSet get(String username, String password) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM Users WHERE username = ? AND password = ?"
        );
        s.setString(1, username);
        s.setString(2, password);
        return s.executeQuery();
    }

    @Override
    public ResultSet get(String username) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM Users WHERE username = ?"
        );
        s.setString(1, username);
        return s.executeQuery();
    }

    @Override
    public boolean update(int userId, long balance) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "UPDATE Users SET balance = ? WHERE user_id = ?"
        );
        s.setLong(1, balance);
        s.setInt(2, userId);
        return s.executeUpdate() > 0;
    }

}
