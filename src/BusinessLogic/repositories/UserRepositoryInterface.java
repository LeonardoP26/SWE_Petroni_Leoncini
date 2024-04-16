package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import BusinessLogic.exceptions.NotEnoughFundsException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.User;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public interface UserRepositoryInterface {


    int insert(@NotNull User user) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    boolean update(@NotNull User user) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    boolean delete(@NotNull User user) throws SQLException, UnableToOpenDatabaseException;

    User get(int userId) throws SQLException, UnableToOpenDatabaseException;

    User get(String username, String password) throws SQLException, UnableToOpenDatabaseException;

    User get(String username) throws SQLException, UnableToOpenDatabaseException;

    boolean update(User user, long amount) throws SQLException, UnableToOpenDatabaseException, NotEnoughFundsException;

}
