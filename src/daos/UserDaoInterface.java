package daos;

import BusinessLogic.UnableToOpenDatabaseException;
import Domain.User;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public interface UserDaoInterface {

    void insert(int id, String username, long balance) throws SQLException, UnableToOpenDatabaseException;

    boolean doesUsernameAlreadyExists(@NotNull String username) throws SQLException, UnableToOpenDatabaseException;

    int getNewId() throws SQLException, UnableToOpenDatabaseException;

    void update(User user) throws SQLException, UnableToOpenDatabaseException;
}
