package daos;

import BusinessLogic.CinemaDatabase;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.ShowTime;
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
    public ResultSet insert(char row, int number, int hallId) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR IGNORE INTO Seats(row, number, hall_id) VALUES (?, ?, ?)"
        );
        s.setString(1, String.valueOf(row));
        s.setInt(2, number);
        s.setInt(3, hallId);
        s.executeUpdate();
        PreparedStatement getId = conn.prepareStatement("SELECT last_insert_rowid()");
        return getId.executeQuery();
    }

    @Override
    public boolean update(int seatId, char row, int number, int hallId) throws SQLException, UnableToOpenDatabaseException {
        try(PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "UPDATE Seats SET row = ?, number = ?, hall_id = ? WHERE seat_id = ?"
        )){
            s.setString(1, String.valueOf(row));
            s.setInt(2, number);
            s.setInt(3, hallId);
            s.setInt(4, seatId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int seatId) throws SQLException, UnableToOpenDatabaseException {
        try(PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "DELETE FROM Seats WHERE seat_id = ?"
        )){
            s.setInt(1, seatId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public ResultSet get(int seatId) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
            "SELECT * FROM Seats WHERE seat_id = ?"
        );
        s.setInt(1, seatId);
        return s.executeQuery();
    }

    @Override
    public ResultSet get(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT ShowTimeSeats.seat_id, row, number, booking_number FROM ShowTimeSeats JOIN Seats ON ShowTimeSeats.seat_id = Seats.seat_id WHERE showtime_id = ?"
        );
        s.setInt(1, showTime.getId());
        return s.executeQuery();
    }

}
