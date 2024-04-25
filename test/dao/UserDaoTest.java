package dao;

import business_logic.CinemaDatabase;
import daos.UserDao;
import daos.UserDaoInterface;
import db.CinemaDatabaseTest;
import domain.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {

    private final UserDaoInterface dao = UserDao.getInstance(CinemaDatabaseTest.DB_URL);

    @Test
    public void insert_Insertion_Success() {
        User newUser = new User("nemo", "nemo");
        assertDoesNotThrow(() -> {
            try(
                    ResultSet res = dao.insert("nemo", "nemo", 0);
                    Connection conn = CinemaDatabase.getConnection(CinemaDatabaseTest.DB_URL)
            ){
                int id = res.getInt(1);
                assertTrue(id > 0);
                PreparedStatement s = conn.prepareStatement(
                        "SELECT * FROM Users WHERE user_id = ?"
                );
                s.setInt(1, id);
                User dbUser = new User(s.executeQuery());
                assertEquals(dbUser.getUsername(), newUser.getUsername());
                assertEquals(dbUser.getPassword(), newUser.getPassword());
                assertEquals(dbUser.getBalance(), newUser.getBalance());
                s = conn.prepareStatement(
                        "DELETE FROM Users WHERE user_id = ?"
                );
                s.setInt(1, id);
                s.executeUpdate();
            }
        });

    }

//    @Test
//    public void update() {
//
//    }
//
//    @Test
//    public void delete() {
//
//    }
//
//    @Test
//    public void get() {
//
//    }
//
//    @Test
//    public void get() {
//
//    }
//
//    @Test
//    public void get() {
//
//    }
}
