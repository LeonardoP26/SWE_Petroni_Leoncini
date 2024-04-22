package daos;

import business_logic.CinemaDatabase;

import java.sql.*;

public class CinemaDao implements CinemaDaoInterface {

    private static CinemaDaoInterface instance = null;

    public static CinemaDaoInterface getInstance(){
        if(instance == null)
            instance = new CinemaDao();
        return instance;
    }

    private CinemaDao() { }


    @Override
    public ResultSet insert(String cinemaName) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR ROLLBACK INTO Cinemas(cinema_name) VALUES (?)"
        );
        s.setString(1, cinemaName);
        s.executeUpdate();
        PreparedStatement getIdStmt = conn.prepareStatement(
                "SELECT last_insert_rowid() as cinema_id where (select last_insert_rowid()) > 0"
        );
        return getIdStmt.executeQuery();
    }

    @Override
    public boolean update(int cinemaId, String cinemaName) throws SQLException {
        try (PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "UPDATE Cinemas SET cinema_name = ? WHERE cinema_id = ?"
        )) {
            s.setString(1, cinemaName);
            s.setInt(2, cinemaId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int cinemaId) throws SQLException {
        try(PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "DELETE FROM Cinemas WHERE cinema_id = ?"
        )){
            s.setInt(1, cinemaId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public ResultSet get(int cinemaId) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM Cinemas WHERE cinema_id = ?"
        );
        s.setInt(1, cinemaId);
        return s.executeQuery();
    }


    @Override
    public ResultSet get() throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement("SELECT * FROM Cinemas");
        return s.executeQuery();
    }

}
