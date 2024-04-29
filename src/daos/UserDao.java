package daos;

import business_logic.Observer;
import business_logic.exceptions.DatabaseFailedException;
import domain.User;
import org.jetbrains.annotations.NotNull;

public interface UserDao extends Dao, Observer<User> {

    void insert(@NotNull User user) throws DatabaseFailedException;

    void update(@NotNull User user) throws DatabaseFailedException;

    void delete(@NotNull User user) throws DatabaseFailedException;

    User get(int userId);

    User get(String username, String password);

    User get(String username);
}
