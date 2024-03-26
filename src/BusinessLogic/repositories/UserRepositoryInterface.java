package BusinessLogic.repositories;

import BusinessLogic.Observer;
import BusinessLogic.Subject;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.User;
import BusinessLogic.UserAlreadyExistsException;

import java.sql.SQLException;

public interface UserRepositoryInterface extends Observer {

    User createAccount(String username) throws SQLException, UserAlreadyExistsException, UnableToOpenDatabaseException;

    @Override
    void update(Subject subject) throws SQLException, UnableToOpenDatabaseException;

}
