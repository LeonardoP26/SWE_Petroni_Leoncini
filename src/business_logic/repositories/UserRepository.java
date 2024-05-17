package business_logic.repositories;

import business_logic.Observer;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.exceptions.NotEnoughFundsException;
import domain.DatabaseEntity;
import domain.User;
import org.jetbrains.annotations.NotNull;
import utils.ThrowingConsumer;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public interface UserRepository extends Observer<DatabaseEntity> {
    void insert(@NotNull User user) throws DatabaseFailedException;

    void update(@NotNull User user, ThrowingConsumer<User> edits) throws NotEnoughFundsException, DatabaseFailedException, InvalidIdException;

    void delete(@NotNull User user) throws DatabaseFailedException, InvalidIdException;

    User get(String username, String password);

    HashMap<Integer, WeakReference<User>> getEntities();
}
