package daos;

import business_logic.CinemaDatabase;
import business_logic.exceptions.DatabaseFailedException;
import domain.Seat;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SeatsDaoImpl implements SeatsDao {

    private static SeatsDao instance = null;

    public static SeatsDao getInstance(){
        if(instance == null)
            instance = new SeatsDaoImpl();
        return instance;
    }

    private SeatsDaoImpl() { }

    @Override
    public void insert(@NotNull Seat seat, int hallId) throws DatabaseFailedException {
        try {
            Connection conn = CinemaDatabase.getConnection();
            try(PreparedStatement s = conn.prepareStatement(
                    "INSERT OR IGNORE INTO Seats(row, number, hall_id) VALUES (?, ?, ?)"
            )) {
                s.setString(1, String.valueOf(seat.getRow()));
                s.setInt(2, seat.getNumber());
                s.setInt(3, hallId);
                if(s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Database insertion failed.");
                try(PreparedStatement getId = conn.prepareStatement(
                        "SELECT last_insert_rowid() as seat_id where (select last_insert_rowid()) > 0"
                )) {
                    try(ResultSet res = getId.executeQuery()){
                        if(!res.next())
                            throw new DatabaseFailedException("Database insertion failed.");
                        seat.setId(res);
                    }
                }
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLiteException e){
            if(e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database insertion failed: this seat already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database insertion failed: ensure seat id, row, number and hall are not null.");
            else if(e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY)
                throw new DatabaseFailedException("Database insertion failed: ensure that hall id is valid.");
            else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(@NotNull Seat seat, int hallId) throws DatabaseFailedException {
        try {
            Connection conn = CinemaDatabase.getConnection();
            try (PreparedStatement s = conn.prepareStatement(
                    "UPDATE Seats SET row = ?, number = ?, hall_id = ? WHERE seat_id = ?"
            )) {
                s.setString(1, String.valueOf(seat.getRow()));
                s.setInt(2, seat.getNumber());
                s.setInt(3, hallId);
                s.setInt(4, seat.getId());
                if(s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Query result is empty.");
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLiteException e){
            if(e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database update failed: this seat already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database update failed: ensure seat id, row, number and hall are not null.");
            else if(e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY)
                throw new DatabaseFailedException("Database update failed: ensure that hall id is valid.");
            else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(@NotNull Seat seat) throws DatabaseFailedException {
        try {
            Connection conn = CinemaDatabase.getConnection();
            try (PreparedStatement s = conn.prepareStatement(
                    "DELETE FROM Seats WHERE seat_id = ?"
            )) {
                s.setInt(1, seat.getId());
                if(s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Deletion failed.");
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Seat get(int seatId) {
        try {
            Connection conn = CinemaDatabase.getConnection();
            try(PreparedStatement s = conn.prepareStatement(
                    "SELECT * FROM Seats WHERE seat_id = ?"
            )) {
                s.setInt(1, seatId);
                try(ResultSet res = s.executeQuery()){
                    if(res.next())
                        return new Seat(res);
                    return null;
                }
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Seat> get(@NotNull ShowTime showTime) {
        try {
            Connection conn = CinemaDatabase.getConnection();
            try(PreparedStatement s = conn.prepareStatement(
                    "SELECT DISTINCT Seats.seat_id, row, number, booking_number FROM (ShowTimes JOIN Seats ON Seats.hall_id = ShowTimes.hall_id) LEFT JOIN Bookings ON (ShowTimes.showtime_id = Bookings.showtime_id AND Seats.seat_id = Bookings.seat_id) WHERE ShowTimes.showtime_id = ?"
            )) {
                s.setInt(1, showTime.getId());
                try(ResultSet res = s.executeQuery()){
                    return getList(res, (seatList) -> {
                        Seat seat = new Seat(res);
                        seat.setBooked(res.getInt("booking_number") > 0);
                        return seat;
                    });
                }
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
