package dao.fake_daos;

import daos.UserDao;
import db.CinemaDatabaseTest;
import domain.User;
import org.jetbrains.annotations.NotNull;

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
        return CinemaDatabaseTest.runQuery(
                "SELECT * FROM Users WHERE username = '%s' AND password = '%s'".formatted(username, password),
                (res) -> {
                    if(!res.next())
                        return null;
                    User user = new User(res);
                    user.setUsername(res.getString("username"));
                    user.setPassword(res.getString("password"));
                    user.setBalance(res.getLong("balance"));
                    return user;
                }
        );
    }
}
