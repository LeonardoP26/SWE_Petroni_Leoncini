package daos;

import business_logic.exceptions.DatabaseFailedException;
import domain.User;
import org.jetbrains.annotations.NotNull;

public interface UserDao extends Dao {

    void insert(@NotNull User user) throws DatabaseFailedException;

    void update(@NotNull User user, @NotNull User copy) throws DatabaseFailedException;

    void delete(@NotNull User user) throws DatabaseFailedException;

    User get(String username, String password);

}
