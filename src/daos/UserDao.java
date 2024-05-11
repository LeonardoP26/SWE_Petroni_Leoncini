package daos;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.User;
import org.jetbrains.annotations.NotNull;

public interface UserDao extends Dao {

    void insert(@NotNull User user) throws DatabaseFailedException;

    void update(@NotNull User user, @NotNull User copy) throws DatabaseFailedException, InvalidIdException;

    void delete(@NotNull User user) throws DatabaseFailedException, InvalidIdException;

    User get(String username, String password);

}
