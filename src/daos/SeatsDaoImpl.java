package daos;

import business_logic.CinemaDatabase;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.DatabaseEntity;
import domain.Hall;
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
import java.util.Objects;

public class SeatsDaoImpl implements SeatsDao {

    private static SeatsDao instance = null;
    private final String dbUrl;

    public static SeatsDao getInstance(){
        return getInstance(CinemaDatabase.DB_URL);
    }

    public static SeatsDao getInstance(String dbUrl){
        if(instance == null) {
            instance = new SeatsDaoImpl(dbUrl);
            return instance;
        }
        if(!Objects.equals(((SeatsDaoImpl) instance).dbUrl, dbUrl))
            instance = new SeatsDaoImpl(dbUrl);
        return instance;
    }

    private SeatsDaoImpl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    @Override
    public void insert(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException {
        if(hall.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try(PreparedStatement s = conn.prepareStatement(
                    "INSERT OR IGNORE INTO Seats(row, number, hall_id) VALUES (?, ?, ?)"
            )) {
                s.setString(1, String.valueOf(seat.getRow()));
                s.setInt(2, seat.getNumber());
                s.setInt(3, hall.getId());
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
    public void update(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException {
        if(hall.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        if(seat.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This seat is not in the database.");
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try (PreparedStatement s = conn.prepareStatement(
                    "UPDATE Seats SET row = ?, number = ?, hall_id = ? WHERE seat_id = ?"
            )) {
                s.setString(1, String.valueOf(seat.getRow()));
                s.setInt(2, seat.getNumber());
                s.setInt(3, hall.getId());
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
    public void delete(@NotNull Seat seat) throws DatabaseFailedException, InvalidIdException {
        if(seat.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This seat is not in the database.");
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try (PreparedStatement s = conn.prepareStatement(
                    "DELETE FROM Seats WHERE seat_id = ?"
            )) {
                s.setInt(1, seat.getId());
                if(s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Deletion failed.");
            }
            try(PreparedStatement s = conn.prepareStatement("SELECT -1 AS seat_id")){
                seat.setId(s.executeQuery());
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Seat get(int seatId) throws InvalidIdException {
        if(seatId < 1)
            throw new InvalidIdException("Id not valid.");
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
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
    public List<Seat> get(@NotNull ShowTime showTime) throws InvalidIdException {
        if(showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database.");
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
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
