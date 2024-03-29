package daos;

import BusinessLogic.CinemaDatabase;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Hall;
import Domain.Movie;
import Domain.Seat;
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
    public void insert(@NotNull Hall hall) throws SQLException, UnableToOpenDatabaseException {
        Connection con = CinemaDatabase.getConnection();
        PreparedStatement s = con.prepareStatement(
                "INSERT OR IGNORE INTO Halls(id, cinemaId, type) VALUES (?, ?, ?)"
        );
        s.setInt(1, hall.getId());
        s.setInt(2, hall.getCinemaId());
        s.setString(3, hall.getHallType().toString());
        s.executeUpdate();
    }

    @Override
    public ResultSet getHall (int hallId) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM Halls WHERE id = ?"
        );
        s.setInt(1, hallId);
        return s.executeQuery();
    }

    @Override
    public ResultSet getHallSeats(Hall hall) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT Seats.id, row, number, isBooked, hallId FROM Halls JOIN Seats ON Halls.id = Seats.hallId WHERE Halls.id = ?"
        );
        s.setInt(1, hall.getId());
        return s.executeQuery();
    }


}
