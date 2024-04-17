package daos;

import BusinessLogic.CinemaDatabase;
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
    public ResultSet insert(char row, int number, int hallId) throws SQLException {
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
    public boolean update(int seatId, char row, int number, int hallId) throws SQLException {
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
    public boolean delete(int seatId) throws SQLException {
        try(PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "DELETE FROM Seats WHERE seat_id = ?"
        )){
            s.setInt(1, seatId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public ResultSet get(int seatId) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
            "SELECT * FROM Seats WHERE seat_id = ?"
        );
        s.setInt(1, seatId);
        return s.executeQuery();
    }

    @Override
    public ResultSet get(@NotNull ShowTime showTime) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT DISTINCT Seats.seat_id, row, number, booking_number FROM (ShowTimes JOIN Seats ON Seats.hall_id = ShowTimes.hall_id) LEFT JOIN Bookings ON (ShowTimes.showtime_id = Bookings.showtime_id AND Seats.seat_id = Bookings.seat_id) WHERE ShowTimes.showtime_id = ?"
        );
        s.setInt(1, showTime.getId());
        return s.executeQuery();
    }

}
