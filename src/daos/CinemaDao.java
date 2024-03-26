package daos;

import BusinessLogic.CinemaDatabase;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Cinema;

import java.sql.*;

public class CinemaDao implements CinemaDaoInterface{

    private static CinemaDaoInterface instance = null;

    public static CinemaDaoInterface getInstance(){
        if(instance == null)
            instance = new CinemaDao();
        return instance;
    }

    private CinemaDao() { }


    @Override
    public void insert(Cinema cinema) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR IGNORE INTO Cinemas(id, name) VALUES (null, ?)"
        );
        s.setString(1, cinema.getName());
        s.executeUpdate();
    }

    @Override
    public ResultSet getCinemaHalls(Cinema cinema) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM Halls WHERE cinemaId = ?"
        );
        s.setInt(1, cinema.getId());
        return s.executeQuery();
    }

}
