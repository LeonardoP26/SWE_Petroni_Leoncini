package BusinessLogic.repositories;

import BusinessLogic.Subject;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.User;
import BusinessLogic.UserAlreadyExistsException;
import daos.UserDao;
import daos.UserDaoInterface;

import java.sql.SQLException;

public class UserRepository implements UserRepositoryInterface {

    private final UserDaoInterface dao = UserDao.getInstance();
    private static UserRepositoryInterface instance = null;

    public static UserRepositoryInterface getInstance(){
        if(instance == null)
            instance = new UserRepository();
        return instance;
    }

    private UserRepository() { }


    @Override
    public User createAccount(String username) throws SQLException, UserAlreadyExistsException, UnableToOpenDatabaseException {
        if (dao.doesUsernameAlreadyExists(username))
            throw new UserAlreadyExistsException("Username already taken.");
        User newUser = new User(dao.getNewId(), username, 0);
        dao.insert(newUser.getId(), newUser.getUsername(), newUser.getBalance());
        return newUser;
    }

    @Override
    public void update(Subject subject) throws SQLException, UnableToOpenDatabaseException {
        if(subject instanceof User){
            dao.update((User) subject);
        }
    }


}
