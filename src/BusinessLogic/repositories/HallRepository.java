package BusinessLogic.repositories;

import BusinessLogic.HallFactory;
import BusinessLogic.exceptions.DatabaseFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Hall;
import Domain.ShowTime;
import daos.HallDao;
import daos.HallDaoInterface;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;
import org.sqlite.SQLiteLimits;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HallRepository extends Repository implements HallRepositoryInterface{

    private final HallDaoInterface dao = HallDao.getInstance();
    private static HallRepositoryInterface instance = null;

    public static HallRepositoryInterface getInstance(){
        if(instance == null)
            instance = new HallRepository();
        return instance;
    }

    private HallRepository() { }


    @Override
    public int insert(@NotNull Hall hall, int cinemaId) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException {
        try(ResultSet res = dao.insert(hall.getHallNumber(), cinemaId, hall.getHallType())){
            if(res.next())
                return res.getInt(1);
            throw new DatabaseFailedException("Database insertion failed.");
        } catch (SQLiteException e){
            if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database insertion failed: this hall already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database insertion failed: ensure hall id, hall number, cinema id and type are not null.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY)
                throw new DatabaseFailedException("Database insertion failed: ensure that cinema id is valid.");
            else throw e; // TODO throw it as DatabaseInsertionFailedException
        }
    }

    @Override
    public boolean update(@NotNull Hall hall, int cinemaId) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException {
        try{
            return dao.update(hall.getId(), hall.getHallNumber(), cinemaId, hall.getHallType());
        } catch (SQLiteException e){
            if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database update failed: this hall already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database update failed: ensure hall id, hall number, cinema id and type are not null.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY)
                throw new DatabaseFailedException("Database update failed: ensure that cinema id is valid.");
            else throw e; // TODO throw it as DatabaseInsertionFailedException
        }
    }

    @Override
    public boolean delete(@NotNull Hall hall) throws SQLException, UnableToOpenDatabaseException {
        return dao.delete(hall.getId());
    }

    @Override
    public Hall get(int hallId) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.get(hallId)){
            if(res.next())
                return HallFactory.createHall(res);
            return null;
        }
    }

    @Override
    public Hall get(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.get(showTime)){
            if(res.next())
                return HallFactory.createHall(res);
            return null;
        }
    }

}
