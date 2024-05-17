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

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class HallDaoImpl implements HallDao {

    private static final HashMap<String, WeakReference<HallDao>> instances = new HashMap<>();
    private final String dbUrl;

    public static @NotNull HallDao getInstance(){
        return getInstance(CinemaDatabase.DB_URL);
    }

    public static @NotNull HallDao getInstance(@NotNull String dbUrl){
        HallDao inst = instances.get(dbUrl) != null ? instances.get(dbUrl).get() : null;
        if(inst != null)
            return inst;
        inst = new HallDaoImpl(dbUrl);
        instances.put(dbUrl, new WeakReference<>(inst));
        return inst;
    }

    private HallDaoImpl(String dbUrl){
        this.dbUrl = dbUrl;
    }

    @Override
    public void insert(@NotNull Hall hall, @NotNull Cinema cinema) throws DatabaseFailedException {
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try (PreparedStatement s = conn.prepareStatement(
                    "INSERT OR IGNORE INTO Halls(hall_number, cinema_id, type) VALUES (?, ?, ?) RETURNING hall_id"
            )) {
                s.setInt(1, hall.getHallNumber());
                s.setInt(2, cinema.getId());
                s.setString(3, hall.getHallType().toString());
                try (ResultSet res = s.executeQuery()) {
                    if(!res.next())
                        throw new DatabaseFailedException("Database insertion failed.");
                    hall.setId(res);
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
    public void update(@NotNull Hall hall, @NotNull Hall copy, @NotNull Cinema cinema) throws DatabaseFailedException {
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try (PreparedStatement s = conn.prepareStatement(
                    "UPDATE Halls SET hall_number = ?, cinema_id = ?, type = ? WHERE hall_id = ?"
            )) {
                s.setInt(1, copy.getHallNumber());
                s.setInt(2, cinema.getId());
                s.setString(3, copy.getHallType().toString());
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
    public void delete(@NotNull Hall hall) throws DatabaseFailedException {
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
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
    public Hall get(@NotNull ShowTime showTime, @NotNull Cinema cinema) {
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try(PreparedStatement s = conn.prepareStatement(
                    "SELECT Halls.hall_id, hall_number, type FROM ShowTimes JOIN Halls ON ShowTimes.hall_id = Halls.hall_id WHERE ShowTimes.showtime_id = ? AND cinema_id = ?"
            )) {
                s.setInt(1, showTime.getId());
                s.setInt(2, cinema.getId());
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
