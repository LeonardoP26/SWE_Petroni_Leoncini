package daos;

import BusinessLogic.CinemaDatabase;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Hall;
import Domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class HallDao implements HallDaoInterface{

    private static HallDaoInterface instance = null;

    public static HallDaoInterface getInstance(){
        if(instance == null)
            instance = new HallDao();
        return instance;
    }

    private HallDao() { }

    @Override
    public ResultSet insert(int hallNumber, int cinemaId, Hall.HallTypes type) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR IGNORE INTO Halls(hallNumber, cinemaId, type) VALUES (?, ?, ?)"
        );
        s.setInt(1, hallNumber);
        s.setInt(2, cinemaId);
        s.setString(3, type.toString());
        s.executeUpdate();
        PreparedStatement getId = conn.prepareStatement("SELECT last_insert_rowid()");
        return getId.executeQuery();
    }

    @Override
    public boolean update(int hallId, int hallNumber, int cinemaId, Hall.HallTypes type) throws SQLException, UnableToOpenDatabaseException {
        try(PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "UPDATE Halls SET hallNumber = ?, cinemaId = ?, type = ? WHERE id = ?"
        )){
            s.setInt(1, hallNumber);
            s.setInt(2, cinemaId);
            s.setString(3, type.toString());
            s.setInt(4, hallId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int hallId) throws SQLException, UnableToOpenDatabaseException {
        try (PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "DELETE FROM Halls WHERE id = ?"
        )){
            s.setInt(1, hallId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public ResultSet get(int hallId) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM Halls WHERE id = ?"
        );
        s.setInt(1, hallId);
        return s.executeQuery();
    }

    @Override
    public ResultSet get(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT Halls.id, hallNumber, type FROM ShowTimes JOIN Halls ON hallId = Halls.id WHERE ShowTimes.id = ?"
        );
        s.setInt(1, showTime.getId());
        return s.executeQuery();
    }


}
