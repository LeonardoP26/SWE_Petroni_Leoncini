package dao.fake_daos;

import business_logic.CinemaDatabase;
import business_logic.exceptions.DatabaseFailedException;
import daos.UserDao;
import db.CinemaDatabaseTest;
import domain.User;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FakeUserDao implements UserDao {

    @Override
    public void insert(@NotNull User user) {

    }

    @Override
    public void update(@NotNull User user, @NotNull User copy) {

    }

    @Override
    public void delete(@NotNull User user) {

    }

    @Override
    public User get(String username, String password) {
        if(Objects.equals(CinemaDatabaseTest.getTestUser1().getUsername(), username)
                && Objects.equals(CinemaDatabaseTest.getTestUser1().getPassword(), password))
            return CinemaDatabaseTest.getTestUser1();
        if(Objects.equals(CinemaDatabaseTest.getTestUser2().getUsername(), username)
                && Objects.equals(CinemaDatabaseTest.getTestUser2().getPassword(), password))
            return CinemaDatabaseTest.getTestUser2();
        return null;
    }
}
