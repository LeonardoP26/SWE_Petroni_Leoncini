package daos;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface CinemaDaoInterface {

    ResultSet insert(String cinemaName) throws SQLException;

    boolean update(int cinemaId, String cinemaName) throws SQLException;

    boolean delete(int cinemaId) throws SQLException;

    ResultSet get(int cinemaId) throws SQLException;

    ResultSet get() throws SQLException;

}
