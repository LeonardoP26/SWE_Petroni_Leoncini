package daos;

import business_logic.CinemaDatabase;
import domain.Hall;
import domain.ShowTime;
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
    public ResultSet insert(int hallNumber, int cinemaId, Hall.HallTypes type) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR IGNORE INTO Halls(hall_number, cinema_id, type) VALUES (?, ?, ?)"
        );
        s.setInt(1, hallNumber);
        s.setInt(2, cinemaId);
        s.setString(3, type.toString());
        s.executeUpdate();
        PreparedStatement getId = conn.prepareStatement(
                "SELECT last_insert_rowid() as hall_id where (select last_insert_rowid()) > 0"
        );
        return getId.executeQuery();
    }

    @Override
    public boolean update(int hallId, int hallNumber, int cinemaId, Hall.HallTypes type) throws SQLException {
        try(PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "UPDATE Halls SET hall_number = ?, cinema_id = ?, type = ? WHERE hall_id = ?"
        )){
            s.setInt(1, hallNumber);
            s.setInt(2, cinemaId);
            s.setString(3, type.toString());
            s.setInt(4, hallId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int hallId) throws SQLException {
        try (PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "DELETE FROM Halls WHERE hall_id = ?"
        )){
            s.setInt(1, hallId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public ResultSet get(int hallId) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM Halls WHERE hall_id = ?"
        );
        s.setInt(1, hallId);
        return s.executeQuery();
    }

    @Override
    public ResultSet get(@NotNull ShowTime showTime) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT Halls.hall_id, hall_number, type FROM ShowTimes JOIN Halls ON ShowTimes.hall_id = Halls.hall_id WHERE ShowTimes.showtime_id = ?"
        );
        s.setInt(1, showTime.getId());
        return s.executeQuery();
    }


}
