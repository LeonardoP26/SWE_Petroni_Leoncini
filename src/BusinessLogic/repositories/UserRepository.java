package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import BusinessLogic.exceptions.NotEnoughFundsException;
import Domain.User;
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
    public int insert(@NotNull User user) throws DatabaseFailedException {
        try (ResultSet res = dao.insert(user.getUsername(), user.getPassword(), user.getBalance())) {
            if (user.getUsername().isBlank())
                user.setUsername(null);
            if (user.getPassword().isBlank())
                user.setPassword(null);
            if (res.next())
                return res.getInt(1);
            throw new DatabaseFailedException("Database insertion failed.");
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
    public boolean update(@NotNull User user) throws DatabaseFailedException {
        try {
            if (user.getUsername().isBlank())
                user.setUsername(null);
            if (user.getPassword().isBlank())
                user.setPassword(null);
            return dao.update(user.getId(), user.getUsername(), user.getPassword(), user.getBalance());
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
    public boolean delete(@NotNull User user) {
        try {
            return dao.delete(user.getId());
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

    @Override
    public boolean update(@NotNull User user, long newBalance) throws NotEnoughFundsException {
        try {
            if (dao.update(user.getId(), newBalance)) {
                user.setBalance(newBalance);
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
