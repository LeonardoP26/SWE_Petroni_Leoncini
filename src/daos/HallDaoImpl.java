package daos;

import business_logic.CinemaDatabase;
import business_logic.HallFactory;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Cinema;
import domain.DatabaseEntity;
import domain.Hall;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HallDaoImpl implements HallDao {

    private static HallDao instance = null;

    public static HallDao getInstance(){
        if(instance == null)
            instance = new HallDaoImpl();
        return instance;
    }

    private HallDaoImpl() { }

    @Override
    public void insert(@NotNull Hall hall, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException {
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");
        try {
            Connection conn = CinemaDatabase.getConnection();
            try (PreparedStatement s = conn.prepareStatement(
                    "INSERT OR IGNORE INTO Halls(hall_number, cinema_id, type) VALUES (?, ?, ?)"
            )) {
                s.setInt(1, hall.getHallNumber());
                s.setInt(2, cinema.getId());
                s.setString(3, hall.getHallType().toString());
                if(s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Database insertion failed.");
                try (PreparedStatement getId = conn.prepareStatement(
                        "SELECT last_insert_rowid() as hall_id where (select last_insert_rowid()) > 0"
                )) {
                    try(ResultSet res = getId.executeQuery()) {
                        if(!res.next())
                            throw new DatabaseFailedException("Database insertion failed.");
                        hall.setId(res);
                    }
                }
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLiteException e){
            if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database insertion failed: this hall already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database insertion failed: ensure hall id, hall number, cinema id and type are not null.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY)
                throw new DatabaseFailedException("Database insertion failed: ensure that cinema id is valid.");
            else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(@NotNull Hall hall, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException {
        if(hall.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");
        try {
            Connection conn = CinemaDatabase.getConnection();
            try (PreparedStatement s = conn.prepareStatement(
                    "UPDATE Halls SET hall_number = ?, cinema_id = ?, type = ? WHERE hall_id = ?"
            )) {
                s.setInt(1, hall.getHallNumber());
                s.setInt(2, cinema.getId());
                s.setString(3, hall.getHallType().toString());
                s.setInt(4, hall.getId());
                if(s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Query result is empty.");
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLiteException e){
            if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database update failed: this hall already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database update failed: ensure hall id, hall number, cinema id and type are not null.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY)
                throw new DatabaseFailedException("Database update failed: ensure that cinema id is valid.");
            else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(@NotNull Hall hall) throws DatabaseFailedException, InvalidIdException {
        if(hall.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        try {
            Connection conn = CinemaDatabase.getConnection();
            try (PreparedStatement s = conn.prepareStatement(
                    "DELETE FROM Halls WHERE hall_id = ?"
            )) {
                s.setInt(1, hall.getId());
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
    public Hall get(int hallId) throws InvalidIdException {
        if (hallId < 0)
            throw new InvalidIdException("This id is not valid.");
        try {
            Connection conn = CinemaDatabase.getConnection();
            try(PreparedStatement s = conn.prepareStatement(
                    "SELECT * FROM Halls WHERE hall_id = ?"
            )) {
                s.setInt(1, hallId);
                try(ResultSet res = s.executeQuery()){
                    if(res.next())
                        return HallFactory.createHall(res);
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
    public Hall get(@NotNull ShowTime showTime) throws InvalidIdException {
        if(showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database.");
        try {
            Connection conn = CinemaDatabase.getConnection();
            try(PreparedStatement s = conn.prepareStatement(
                    "SELECT Halls.hall_id, hall_number, type FROM ShowTimes JOIN Halls ON ShowTimes.hall_id = Halls.hall_id WHERE ShowTimes.showtime_id = ?"
            )) {
                s.setInt(1, showTime.getId());
                try (ResultSet res = s.executeQuery()) {
                    if(res.next())
                        return HallFactory.createHall(res);
                    return null;
                }
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

    }


}
