package business_logic.repositories;

import business_logic.exceptions.DatabaseFailedException;
import domain.User;
import daos.UserDao;
import daos.UserDaoInterface;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository extends Repository implements UserRepositoryInterface {

    private final UserDaoInterface dao = UserDao.getInstance();
    private static UserRepositoryInterface instance = null;

    public static UserRepositoryInterface getInstance() {
        if (instance == null)
            instance = new UserRepository();
        return instance;
    }

    private UserRepository() { }

    @Override
    public void insert(@NotNull User user) throws DatabaseFailedException {
        if (user.getUsername().isBlank())
            user.setUsername(null);
        if (user.getPassword().isBlank())
            user.setPassword(null);
        try (ResultSet res = dao.insert(user.getUsername(), user.getPassword(), user.getBalance())) {
            if (isQueryResultEmpty(res))
                throw new DatabaseFailedException("Database insertion failed.");
            user.setId(res);
        } catch (SQLiteException e) {
            if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database insertion failed: username already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database insertion failed: username and password can not be null");
            else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(@NotNull User user) throws DatabaseFailedException {
        try {
            if (user.getUsername().isBlank())
                user.setUsername(null);
            if (user.getPassword().isBlank())
                user.setPassword(null);
            if(!dao.update(user.getId(), user.getUsername(), user.getPassword(), user.getBalance()))
                throw new DatabaseFailedException("Update failed.");
        } catch (SQLiteException e) {
            if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database update failed: username already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database update failed: username and password can not be null");
            else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void delete(@NotNull User user) throws DatabaseFailedException {
        try {
            if(!dao.delete(user.getId()))
                throw new DatabaseFailedException("Deletion failed.");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public User get(int userId) {
        try (ResultSet res = dao.get(userId)) {
            if (res.next())
                return new User(res);
            return null;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public User get(String username, String password) {
        try (ResultSet res = dao.get(username, password)) {
            if (res.next()) {
                return new User(res);
            }
            return null;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public User get(String username) {
        try (ResultSet res = dao.get(username)) {
            if (res.next())
                return new User(res);
            return null;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }



}
