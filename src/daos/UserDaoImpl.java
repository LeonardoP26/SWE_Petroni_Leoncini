package daos;

import business_logic.CinemaDatabase;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.DatabaseEntity;
import domain.User;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoImpl implements UserDao {

    private static UserDao instance = null;
    private final String dbUrl;

    public static UserDao getInstance(){
        if(instance == null)
            instance = new UserDaoImpl();
        return instance;
    }

    public static UserDao getInstance(String dbUrl){
        if(instance == null)
            instance = new UserDaoImpl(dbUrl);
        return instance;
    }

    private UserDaoImpl() {
        this(CinemaDatabase.DB_URL);
    }
    private UserDaoImpl(String dbUrl){
        this.dbUrl = dbUrl;
    }

    @Override
    public void insert(@NotNull User user) throws DatabaseFailedException {
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try(PreparedStatement s = conn.prepareStatement(
                    "INSERT OR ROLLBACK INTO Users(username, password, balance) VALUES (?, ?, ?)"
            )) {
                s.setString(1, user.getUsername());
                s.setString(2, user.getPassword());
                s.setLong(3, user.getBalance());
                s.executeUpdate();
                try (PreparedStatement getId = conn.prepareStatement(
                        "SELECT last_insert_rowid() as user_id where (select last_insert_rowid()) > 0"
                )) {
                    try (ResultSet res = getId.executeQuery()) {
                        if(!res.next())
                            throw new DatabaseFailedException("Database insertion failed.");
                        user.setId(res);

                    }
                }
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLiteException e) {
            if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Username already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Username and password can not be blank.");
            else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(@NotNull User user) throws DatabaseFailedException, InvalidIdException {
        if(user.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This user is not in the database.");
        try {
            Connection conn = CinemaDatabase.getConnection();
            try (PreparedStatement s = conn.prepareStatement(
                    "UPDATE Users SET username = ?, password = ?, balance = ? WHERE user_id = ?"
            )) {
                s.setString(1, user.getUsername());
                s.setString(2, user.getPassword());
                s.setLong(3, user.getBalance());
                s.setInt(4, user.getId());
                if(s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Update failed.");
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLiteException e) {
            if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Username already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Username and password can not be null");
            else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void delete(@NotNull User user) throws DatabaseFailedException, InvalidIdException {
        if(user.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This user is not in the database.");
        try {
            Connection conn = CinemaDatabase.getConnection();
            try (PreparedStatement s = conn.prepareStatement(
                    "DELETE FROM Users WHERE user_id = ?"
            )) {
                s.setInt(1, user.getId());
                if(s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Deletion failed.");
            }
            try (PreparedStatement s = conn.prepareStatement(
                    "SELECT -1 AS user_id"
            )) {
                user.setId(s.executeQuery());
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public User get(int userId) {
        try(Connection conn = CinemaDatabase.getConnection()) {
            try(PreparedStatement s = conn.prepareStatement(
                    "SELECT * FROM Users WHERE user_id = ?"
            )) {
                s.setInt(1, userId);
                try(ResultSet res = s.executeQuery()) {
                    if (res.next())
                        return new User(res);
                    return null;
                }
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public User get(String username, String password) {
        try (Connection conn = CinemaDatabase.getConnection()) {
            try (PreparedStatement s = conn.prepareStatement(
                    "SELECT * FROM Users WHERE username = ? AND password = ?"
            )) {
                s.setString(1, username);
                s.setString(2, password);
                try(ResultSet res = s.executeQuery()){
                    if(res.next())
                        return new User(res);
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public User get(String username) {
        try (Connection conn = CinemaDatabase.getConnection()) {
            try (PreparedStatement s = conn.prepareStatement(
                    "SELECT * FROM Users WHERE username = ?"
            )) {
                s.setString(1, username);
                try (ResultSet res = s.executeQuery()) {
                    if (res.next())
                        return new User(res);
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

}
