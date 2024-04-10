package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseInsertionFailedException;
import BusinessLogic.exceptions.NotEnoughFundsException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Booking;
import Domain.Seat;
import Domain.ShowTime;
import Domain.User;
import daos.UserDao;
import daos.UserDaoInterface;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository extends Repository implements UserRepositoryInterface {

    private final UserDaoInterface dao = UserDao.getInstance();
    private static UserRepositoryInterface instance = null;

    public static UserRepositoryInterface getInstance(){
        if(instance == null)
            instance = new UserRepository();
        return instance;
    }

    private UserRepository() { }

    @Override
    public int insert(@NotNull User user) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException {
        try(ResultSet res = dao.insert(user.getUsername(), user.getPassword(), user.getBalance())){
            if(res.next())
                return res.getInt(1);
            throw new DatabaseInsertionFailedException("Database insertion failed: try with a different username");
        }
    }

    @Override
    public boolean update(@NotNull User user) throws SQLException, UnableToOpenDatabaseException {
        return dao.update(user.getId(), user.getUsername(), user.getPassword(), user.getBalance());
    }

    @Override
    public boolean delete(@NotNull User user) throws SQLException, UnableToOpenDatabaseException {
        return dao.delete(user.getId());
    }

    @Override
    public User get(int userId) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.get(userId)){
            if(res.next())
                return new User(res);
            return null;
        }
    }

    @Override
    public User get(String username, String password) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.get(username, password)){
            if(res.next()){
                return new User(res);
            }
            return null;
        }
    }

    @Override
    public User get(String username) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.get(username)){
            if(res.next())
                return new User(res);
            return null;
        }
    }

    @Override
    public boolean update(@NotNull User user, long amount) throws SQLException, UnableToOpenDatabaseException, NotEnoughFundsException {
        long newBalance = user.getBalance() + amount;
        if(dao.update(user.getId(), newBalance)) {
            user.setBalance(newBalance);
            return true;
        }
        return false;
    }



}
