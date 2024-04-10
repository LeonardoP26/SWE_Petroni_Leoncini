package daos;

import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface UserDaoInterface {

    ResultSet insert(String username, String password, long balance) throws SQLException, UnableToOpenDatabaseException;

    boolean update(int userId, String username, String password, long balance) throws SQLException, UnableToOpenDatabaseException;

    boolean delete(int userId) throws SQLException, UnableToOpenDatabaseException;

    ResultSet get(int userId) throws SQLException, UnableToOpenDatabaseException;

    ResultSet doesUsernameAlreadyExists(@NotNull String username) throws SQLException, UnableToOpenDatabaseException;

    ResultSet get(String username, String password) throws SQLException, UnableToOpenDatabaseException;

    ResultSet get(String username) throws SQLException, UnableToOpenDatabaseException;

    boolean update(int userId, long balance) throws SQLException, UnableToOpenDatabaseException;
}
