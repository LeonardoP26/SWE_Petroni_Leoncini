package daos;

import business_logic.CinemaDatabase;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Cinema;
import domain.DatabaseEntity;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class CinemaDaoImpl implements CinemaDao {

    private static final HashMap<String, CinemaDao> instances = new HashMap<>();
    private final String dbUrl;

    public static CinemaDao getInstance(){
        return getInstance(CinemaDatabase.DB_URL);
    }

    public static CinemaDao getInstance(String dbUrl){
        if(instances.containsKey(dbUrl))
            return instances.get(dbUrl);
        CinemaDao newInstance = new CinemaDaoImpl(dbUrl);
        instances.put(dbUrl, newInstance);
        return newInstance;
    }

    private CinemaDaoImpl(String dbUrl){
        this.dbUrl = dbUrl;
    }


    @Override
    public void insert(@NotNull Cinema cinema) throws DatabaseFailedException {
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try (PreparedStatement s = conn.prepareStatement(
                    "INSERT OR ROLLBACK INTO Cinemas(cinema_name) VALUES (?)"
            )) {
                s.setString(1, cinema.getName());
                if (s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Database insertion failed.");
                try (PreparedStatement getIdStmt = conn.prepareStatement(
                        "SELECT last_insert_rowid() as cinema_id where (select last_insert_rowid()) > 0"
                )) {
                    try (ResultSet res = getIdStmt.executeQuery()) {
                        if(!res.next())
                            throw new DatabaseFailedException("Database insertion failed.");
                        cinema.setId(res);
                    }
                }
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLiteException e){
            if(e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database insertion failed: this cinema already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database insertion failed: ensure cinema's id and name are not null.");
            else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(@NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException {
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try (PreparedStatement s = conn.prepareStatement(
                    "UPDATE OR ROLLBACK Cinemas SET cinema_name = ? WHERE cinema_id = ?"
            )) {
                s.setString(1, cinema.getName());
                s.setInt(2, cinema.getId());
                if(s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Query result is empty.");
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLiteException e){
            if(e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("This cinema already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Ensure cinema's id and name are not null.");
            else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(@NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException {
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try (PreparedStatement s = conn.prepareStatement(
                    "DELETE FROM Cinemas WHERE cinema_id = ?"
            )) {
                s.setInt(1, cinema.getId());
                if (s.executeUpdate() == 0)
                    throw new DatabaseFailedException("Deletion failed.");
            }try (PreparedStatement s = conn.prepareStatement(
                    "SELECT -1 AS cinema_id"
            )) {
                cinema.setId(s.executeQuery());
            } finally {
                if(conn.getAutoCommit())
                    conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Cinema> get() {
        try {
            Connection conn = CinemaDatabase.getConnection(dbUrl);
            try(PreparedStatement s = conn.prepareStatement("SELECT * FROM Cinemas")) {
                try(ResultSet res = s.executeQuery()){
                    return getList(res, (cinemaList) -> new Cinema(res));
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
