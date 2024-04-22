package business_logic.repositories;

import business_logic.exceptions.DatabaseFailedException;
import domain.Cinema;
import daos.CinemaDao;
import daos.CinemaDaoInterface;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

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
    public void insert(Cinema cinema) throws DatabaseFailedException {
        try (ResultSet res = dao.insert(cinema.getName())) {
            if(isQueryResultEmpty(res))
                throw new DatabaseFailedException("Database insertion failed: Record already present");
            cinema.setId(res);
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
    public void update(@NotNull Cinema cinema) throws DatabaseFailedException {
        try{
            if(!dao.update(cinema.getId(), cinema.getName()))
                throw new DatabaseFailedException("Query result is empty.");
        } catch (SQLiteException e){
            if(e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database update failed: this cinema already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database update failed: ensure cinema's id and name are not null.");
            else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(@NotNull Cinema cinema) throws DatabaseFailedException {
        try{
            if(!dao.delete(cinema.getId()))
                throw new DatabaseFailedException("Deletion failed.");
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Cinema get(int cinemaId) {
        try(ResultSet res = dao.get(cinemaId)){
            if(res.next())
                return new Cinema(res);
            return null;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Cinema> get() {
        try(ResultSet res = dao.get()){
            return getList(res, (cinemaList) -> new Cinema(res));
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }


}
