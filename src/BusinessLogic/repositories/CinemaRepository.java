package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Cinema;
import daos.CinemaDao;
import daos.CinemaDaoInterface;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;
import org.sqlite.SQLiteLimits;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CinemaRepository extends Repository implements CinemaRepositoryInterface {

    private final CinemaDaoInterface dao = CinemaDao.getInstance();

    private static CinemaRepositoryInterface instance = null;
    public static CinemaRepositoryInterface getInstance(){
        if(instance == null)
            instance = new CinemaRepository();
        return instance;
    }

    private CinemaRepository() { }

    @Override
    public int insert(Cinema cinema) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException {
        try (ResultSet res = dao.insert(cinema.getName())) {
            if(res.next())
                return res.getInt(1);
            throw new DatabaseFailedException("Database insertion failed: Record already present");
        } catch (SQLiteException e){
            if(e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database insertion failed: this cinema already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database insertion failed: ensure cinema's id and name are not null.");
            else throw e; // TODO throw it as DatabaseInsertionFailedException
        }
    }

    @Override
    public boolean update(@NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException {
        try{
            return dao.update(cinema.getId(), cinema.getName());
        } catch (SQLiteException e){
            if(e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database update failed: this cinema already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database update failed: ensure cinema's id and name are not null.");
            else throw e; // TODO throw it as DatabaseInsertionFailedException
        }
    }

    @Override
    public boolean delete(@NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException {
        return dao.delete(cinema.getId());
    }

    @Override
    public Cinema get(int cinemaId) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.get(cinemaId)){
            if(res.next())
                return new Cinema(res);
            return null;
        }
    }

    @Override
    public List<Cinema> get() throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.get()){
            return getList(res, () -> new Cinema(res));
        }
    }


}
