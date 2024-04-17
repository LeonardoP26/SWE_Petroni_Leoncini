package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import BusinessLogic.exceptions.NotEnoughFundsException;
import Domain.User;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public interface UserRepositoryInterface {


    int insert(@NotNull User user) throws DatabaseFailedException;

    boolean update(@NotNull User user) throws DatabaseFailedException;

    boolean delete(@NotNull User user);

    User get(int userId);

    User get(String username, String password);

    User get(String username);

    boolean update(User user, long amount) throws NotEnoughFundsException;

}
