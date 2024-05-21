package dao;

import business_logic.exceptions.DatabaseFailedException;
import daos.UserDao;
import daos.UserDaoImpl;
import db.CinemaDatabaseTest;
import domain.DatabaseEntity;
import domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {

    private final UserDao userDao = UserDaoImpl.getInstance(CinemaDatabaseTest.DB_URL);

    @BeforeEach
    public void seatUpEach(){
        CinemaDatabaseTest.setUp();
    }

    @AfterEach
    public void tearDownEach(){
        CinemaDatabaseTest.tearDown();
    }

    @Test
    public void insertUser_success(){
        User newUser = new User("abc", "abc");
        assertDoesNotThrow(() -> userDao.insert(newUser));
        assertTrue(newUser.getId() > 0);
        assertNotNull(
                CinemaDatabaseTest.runQuery(
                        "SELECT * FROM Users WHERE user_id = %d AND username = '%s' AND password = '%s'"
                                .formatted(newUser.getId(), newUser.getUsername(), newUser.getPassword()),
                        (res) -> {
                            if (!res.next())
                                return null;
                            return new User(res);
                        })
        );
    }

    @Test
    public void insertUser_withSameUsername_throwsDatabaseFailedException(){
        User newUser = new User(CinemaDatabaseTest.getTestUser1().getUsername(), "abc");
        assertThrows(DatabaseFailedException.class, () -> userDao.insert(newUser));
        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, newUser.getId());
        assertEquals(
                (int) CinemaDatabaseTest.runQuery(
                        "SELECT COUNT(username) FROM Users WHERE username = '%s'".formatted(CinemaDatabaseTest.getTestUser1().getUsername()),
                        (res) -> {
                            if(!res.next())
                                return 0;
                            return res.getInt(1);
                        }
                ), 1
        );
    }

    @Test
    public void insertUser_withNullValues_throwsDatabaseFailedException(){
        User newUser = new User(null, null);
        assertThrows(DatabaseFailedException.class, () -> userDao.insert(newUser));
        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, newUser.getId());
        assertNull(
            CinemaDatabaseTest.runQuery(
                "SELECT * FROM Users WHERE username = '%s' AND password = '%s'"
                        .formatted(newUser.getUsername(), newUser.getPassword()),
                (res) -> {
                    if (!res.next())
                        return null;
                    return new User(res);
                })
        );
    }

    @Test
    public void updateUser_success(){
        User copy = new User(CinemaDatabaseTest.getTestUser1());
        copy.setUsername("abc");
        copy.setPassword("abc");
        assertDoesNotThrow(() -> copy.setBalance(10));
        assertDoesNotThrow(() -> userDao.update(CinemaDatabaseTest.getTestUser1(), copy));
        User dbUser = CinemaDatabaseTest.runQuery(
            "SELECT * FROM Users WHERE username = '%s' AND password = '%s' AND balance = %d"
                    .formatted(copy.getUsername(), copy.getPassword(), copy.getBalance()),
            (res) -> {
                if (!res.next())
                    return null;
                User user = new User(res);
                user.setUsername(res.getString("username"));
                user.setPassword(res.getString("password"));
                user.setBalance(res.getLong("balance"));
                return user;
            }
        );
        assertNotNull(dbUser);
        assertEquals(dbUser.getUsername(), copy.getUsername());
        assertEquals(dbUser.getPassword(), copy.getPassword());
        assertEquals(dbUser.getBalance(), copy.getBalance());
    }

    @Test
    public void updateUser_toSameValues_throwsDatabaseFailedException(){
        User copy = new User(CinemaDatabaseTest.getTestUser1());
        copy.setUsername(CinemaDatabaseTest.getTestUser2().getUsername());
        assertThrows(DatabaseFailedException.class, () -> userDao.update(CinemaDatabaseTest.getTestUser1(), copy));
    }

    @Test
    public void updateUser_toNullValues_throwsDatabaseFailedException(){
        User copy = new User(CinemaDatabaseTest.getTestUser1());
        copy.setUsername(null);
        copy.setPassword(null);
        assertThrows(DatabaseFailedException.class, () -> userDao.update(CinemaDatabaseTest.getTestUser1(), copy));
    }

    @Test
    public void deleteUser_success(){
        assertDoesNotThrow(() -> userDao.delete(CinemaDatabaseTest.getTestUser1()));
        assertTrue(() ->
                CinemaDatabaseTest.runQuery(
                        "SELECT * FROM Users WHERE user_id = %d".formatted(CinemaDatabaseTest.getTestUser1().getId()),
                        (res) -> !res.next()
                ));
    }

    @Test
    public void deleteUser_notInDatabase_throwsDatabaseFailedException(){
        assertThrows(DatabaseFailedException.class, () -> userDao.delete(new User("abc", "abc")));
    }

    @Test
    public void getUser_success(){
        User testUser1 = CinemaDatabaseTest.getTestUser1();
        User dbUser = assertDoesNotThrow(() -> userDao.get(testUser1.getUsername(), testUser1.getPassword()));
        assertEquals(dbUser.getId(), testUser1.getId());
        assertEquals(dbUser.getUsername(), testUser1.getUsername());
        assertEquals(dbUser.getPassword(), testUser1.getPassword());
        assertEquals(dbUser.getBalance(), testUser1.getBalance());
    }

}
