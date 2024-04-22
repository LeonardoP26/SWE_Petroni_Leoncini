package daos;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface UserDaoInterface {

    ResultSet insert(String username, String password, long balance) throws SQLException;

    boolean update(int userId, String username, String password, long balance) throws SQLException;

    boolean delete(int userId) throws SQLException;

    ResultSet get(int userId) throws SQLException;

    ResultSet get(String username, String password) throws SQLException;

    ResultSet get(String username) throws SQLException;
}
