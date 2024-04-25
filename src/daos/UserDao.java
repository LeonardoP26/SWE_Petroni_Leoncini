package daos;

import business_logic.CinemaDatabase;

import java.sql.*;

public class UserDao implements UserDaoInterface{

    private static UserDaoInterface instance = null;
    private final String dbUrl;

    public static UserDaoInterface getInstance(){
        if(instance == null)
            instance = new UserDao();
        return instance;
    }

    public static UserDaoInterface getInstance(String dbUrl){
        if(instance == null)
            instance = new UserDao(dbUrl);
        return instance;
    }

    private UserDao() {
        this(CinemaDatabase.DB_URL);
    }
    private UserDao(String dbUrl){
        this.dbUrl = dbUrl;
    }

    @Override
    public ResultSet insert(String username, String password, long balance) throws SQLException {
        Connection conn = CinemaDatabase.getConnection(dbUrl);
        PreparedStatement s = conn.prepareStatement(
                "INSERT INTO Users(username, password, balance) VALUES (?, ?, ?)"
        );
        s.setString(1, username);
        s.setString(2, password);
        s.setLong(3, balance);
        s.executeUpdate();
        PreparedStatement getId = conn.prepareStatement(
                "SELECT last_insert_rowid() as user_id where (select last_insert_rowid()) > 0"
        );
        return getId.executeQuery();
    }

    @Override
    public boolean update(int userId, String username, String password, long balance) throws SQLException {
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
    public boolean delete(int userId) throws SQLException {
        try (PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "DELETE FROM Users WHERE user_id = ?"
        )) {
            s.setInt(1, userId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public ResultSet get(int userId) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM Users WHERE user_id = ?"
        );
        s.setInt(1, userId);
        return s.executeQuery();
    }

    @Override
    public ResultSet get(String username, String password) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM Users WHERE username = ? AND password = ?"
        );
        s.setString(1, username);
        s.setString(2, password);
        return s.executeQuery();
    }

    @Override
    public ResultSet get(String username) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM Users WHERE username = ?"
        );
        s.setString(1, username);
        return s.executeQuery();
    }

}
