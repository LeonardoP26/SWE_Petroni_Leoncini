package unit_test.repositories.fake_repositories;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.exceptions.NotEnoughFundsException;
import business_logic.repositories.UserRepository;
import domain.DatabaseEntity;
import domain.User;
import org.jetbrains.annotations.NotNull;
import utils.ThrowingConsumer;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class FakeUserRepository implements UserRepository {

    @Override
    public void insert(@NotNull User user) {

    }

    @Override
    public void update(@NotNull User user, ThrowingConsumer<User> edits) {

    }

    @Override
    public void delete(@NotNull User user) {

    }

    @Override
    public User get(String username, String password) {
        return null;
    }

    @Override
    public HashMap<Integer, WeakReference<User>> getEntities() {
        return null;
    }

    @Override
    public void update(@NotNull DatabaseEntity entity) {

    }
}
