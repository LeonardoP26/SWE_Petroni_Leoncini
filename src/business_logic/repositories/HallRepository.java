package business_logic.repositories;

import business_logic.HallFactory;
import business_logic.exceptions.DatabaseFailedException;
import domain.Hall;
import domain.ShowTime;
import daos.HallDao;
import daos.HallDaoInterface;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

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
    public void insert(@NotNull Hall hall, int cinemaId) throws DatabaseFailedException {
        try(ResultSet res = dao.insert(hall.getHallNumber(), cinemaId, hall.getHallType())){
            if(isQueryResultEmpty(res))
                throw new DatabaseFailedException("Database insertion failed.");
            hall.setId(res);
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
    public void update(@NotNull Hall hall, int cinemaId) throws DatabaseFailedException {
        try{
            if(!dao.update(hall.getId(), hall.getHallNumber(), cinemaId, hall.getHallType()))
                throw new DatabaseFailedException("Query result is empty.");
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
        try{
            if(!dao.delete(hall.getId()))
                throw new DatabaseFailedException("Deletion failed.");
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Hall get(int hallId) {
        try(ResultSet res = dao.get(hallId)){
            if(res.next())
                return HallFactory.createHall(res);
            return null;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Hall get(@NotNull ShowTime showTime) {
        try(ResultSet res = dao.get(showTime)){
            if(res.next())
                return HallFactory.createHall(res);
            return null;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

}
