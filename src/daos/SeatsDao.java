package daos;

import BusinessLogic.CinemaDatabase;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Seat;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class SeatsDao implements SeatsDaoInterface{

    private static SeatsDaoInterface instance = null;

    public static SeatsDaoInterface getInstance(){
        if(instance == null)
            instance = new SeatsDao();
        return instance;
    }

    private SeatsDao() { }

    @Override
    public void insert(@NotNull Seat seat) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR IGNORE INTO Seats(row, number, isBooked, hallId) VALUES (?, ?, ?, ?)"
        );
        s.setString(1, String.valueOf(seat.getRow()));
        s.setInt(2, seat.getNumber());
        s.setBoolean(3, seat.isBooked());
        s.setInt(4, seat.getHallId());
        s.executeUpdate();
    }

    @Override
    public void update(@NotNull Seat seat) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "UPDATE Seats SET row = ?, number = ?, hallId = ?, isBooked = ? WHERE id = ?"
        );
        s.setString(1, String.valueOf(seat.getRow()));
        s.setInt(2, seat.getNumber());
        s.setInt(3, seat.getHallId());
        s.setBoolean(4, seat.isBooked());
        s.setInt(5, seat.getId());
        s.executeUpdate();
    }

    @Override
    public Seat getSeat(int id) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        Statement s = conn.createStatement();
        ResultSet res = s.executeQuery("SELECT * FROM Seats WHERE id = :id");
        return new Seat(res.getInt(1),res.getString(2).charAt(0), res.getInt(3), res.getInt(4), res.getBoolean(5));
    }
}
