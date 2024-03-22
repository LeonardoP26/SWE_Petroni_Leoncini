package daos;

import BusinessLogic.CinemaDatabase;
import Domain.Hall;
import Domain.Seat;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SeatsDao implements SeatsDaoInterface{

    @Override
    public void insert(@NotNull Seat seat) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR IGNORE INTO Seats(row, number, isBooked, hallId) VALUES (?, ?, ?, ?)"
        );
        s.setString(1, String.valueOf(seat.getRow()));
        s.setInt(2, seat.getNumber());
        s.setBoolean(3, seat.isBooked());
        s.setInt(4, seat.getHall().getId());
        s.executeUpdate();
    }

}
