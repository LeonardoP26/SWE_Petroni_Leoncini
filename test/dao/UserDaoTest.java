package dao;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.exceptions.NotEnoughFundsException;
import daos.UserDao;
import daos.UserDaoImpl;
import db.CinemaDatabaseTest;
import domain.DatabaseEntity;
import domain.User;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {

    private final UserDao userDao = UserDaoImpl.getInstance(CinemaDatabaseTest.DB_URL);
    private User insertedUser;

    @BeforeEach
    public void setUpEach(){
        CinemaDatabaseTest.setUp();
        insertedUser = new User("insertedUser", "insertedUser");
    }

    @AfterEach
    public void tearDownEach(){
        CinemaDatabaseTest.tearDown();
    }

    private @Nullable User get(String username, String password) {
        try (
                Connection conn = CinemaDatabaseTest.getConnection();
                PreparedStatement s = conn.prepareStatement("SELECT * FROM Users WHERE username = ? AND password = ?")
        ) {
            s.setString(1, username);
            s.setString(2, password);
            try (ResultSet res = s.executeQuery()) {
                if (res.isBeforeFirst())
                    return new User(res);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void insertUser_success(){
        assertDoesNotThrow(() -> userDao.insert(insertedUser));
        User dbInsertedUser = get(insertedUser.getUsername(), insertedUser.getPassword());
        assertNotNull(dbInsertedUser);
        assertTrue(insertedUser.getId() > 0);
        assertTrue(dbInsertedUser.getId() > 0);
        assertEquals(insertedUser.getId(), dbInsertedUser.getId());
        assertEquals(insertedUser.getUsername(), dbInsertedUser.getUsername());
        assertEquals(insertedUser.getPassword(), dbInsertedUser.getPassword());
        assertEquals(insertedUser.getBalance(), dbInsertedUser.getBalance());
    }

    @Test
    public void insertUser_sameUsername_throwException(){
        assertThrows(DatabaseFailedException.class, () -> userDao.insert(new User("user1", "user1")));
    }

    @Test
    public void insertUser_usernameAndPasswordNull_throwException(){
        User nullUsername = new User(null, insertedUser.getPassword());
        User nullPassword = new User(insertedUser.getUsername(), null);
        assertThrows(DatabaseFailedException.class, () -> userDao.insert(nullUsername));
        assertThrows(DatabaseFailedException.class, () -> userDao.insert(nullPassword));
        assertNull(get(nullUsername.getUsername(), nullUsername.getPassword()));
        assertNull(get(nullPassword.getUsername(), nullPassword.getPassword()));
    }

    @Test
    public void updateUser_success(){
        assertDoesNotThrow(() -> userDao.insert(insertedUser));
        insertedUser.setUsername("updatedUser");
        insertedUser.setPassword("updatedUser");
        assertDoesNotThrow(() -> insertedUser.setBalance(1));
        assertDoesNotThrow(() -> userDao.update(insertedUser));
        User updatedUser = get(insertedUser.getUsername(), insertedUser.getPassword());
        assertNotNull(updatedUser);
        assertEquals(updatedUser.getUsername(), insertedUser.getUsername());
        assertEquals(updatedUser.getPassword(), insertedUser.getPassword());
        assertEquals(updatedUser.getBalance(), insertedUser.getBalance());
    }

    @Test
    public void updateUser_toSameUsername_throwException(){
        assertDoesNotThrow(() -> userDao.insert(insertedUser));
        String oldUsername = insertedUser.getUsername();
        insertedUser.setUsername("user1");
        assertThrows(DatabaseFailedException.class, () -> userDao.update(insertedUser));
        assertNotNull(get(oldUsername, insertedUser.getPassword()));
    }

    @Test
    public void updateUser_toNotValidValues_throwExceptions(){
        assertDoesNotThrow(() -> userDao.insert(insertedUser));
        String oldUsername = insertedUser.getUsername();
        String oldPassword = insertedUser.getPassword();
        insertedUser.setUsername(null);
        assertThrows(DatabaseFailedException.class, () -> userDao.update(insertedUser));
        assertNotNull(get(oldUsername, oldPassword));
        insertedUser.setUsername(oldUsername);
        insertedUser.setPassword(null);
        assertThrows(DatabaseFailedException.class, () -> userDao.update(insertedUser));
        assertNotNull(get(oldUsername, oldPassword));
        insertedUser.setPassword(oldPassword);
        assertThrows(InvalidIdException.class, () -> userDao.update(new User(oldUsername, oldPassword)));
        assertThrows(NotEnoughFundsException.class, () -> insertedUser.setBalance(-1));
    }

    @Test
    @Order(7)
    public void getUser_success(){
        assertDoesNotThrow(() -> userDao.insert(insertedUser));
        User user = assertDoesNotThrow(() -> userDao.get(insertedUser.getUsername(), insertedUser.getPassword()));
        assertNotNull(user);
        assertEquals(insertedUser.getUsername(), user.getUsername());
        assertEquals(insertedUser.getPassword(), user.getPassword());
        assertEquals(insertedUser.getBalance(), user.getBalance());
    }

    @Test
    @Order(8)
    public void deleteUser_success(){
        assertDoesNotThrow(() -> userDao.insert(insertedUser));
        assertDoesNotThrow(() -> userDao.delete(insertedUser));
        assertNull(get(insertedUser.getUsername(), insertedUser.getPassword()));
        assertEquals(DatabaseEntity.ENTITY_WITHOUT_ID, insertedUser.getId());
    }

    @Test
    @Order(9)
    public void deleteUser_notValidValues_throwException(){
        User nemo = new User(insertedUser.getUsername(), insertedUser.getPassword());
        assertThrows(InvalidIdException.class, () -> userDao.delete(nemo));
    }

}
