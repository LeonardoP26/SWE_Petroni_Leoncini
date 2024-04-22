package business_logic.repositories;

import business_logic.HallFactory;
import business_logic.exceptions.DatabaseFailedException;
import domain.Hall;
import domain.Movie;
import domain.ShowTime;
import daos.ShowTimeDao;
import daos.ShowTimeDaoInterface;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ShowTimeRepository extends Repository implements ShowTimeRepositoryInterface {

    private final static ShowTimeDaoInterface dao = ShowTimeDao.getInstance();
    private static ShowTimeRepositoryInterface instance = null;

    public static ShowTimeRepositoryInterface getInstance(){
        if(instance == null)
            instance = new ShowTimeRepository();
        return instance;
    }

    private ShowTimeRepository() { }


    @Override
    public void insert(@NotNull ShowTime showTime) throws DatabaseFailedException {
        try(ResultSet res = dao.insert(showTime.getMovie().getId(), showTime.getHall().getId(), showTime.getDate())){
            if(isQueryResultEmpty(res))
                throw new DatabaseFailedException("Database insertion failed.");
            showTime.setId(res);
        } catch (SQLiteException e){
            if(e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code)
                throw new DatabaseFailedException("Database insertion failed: this showtime already exists.");
            else if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL.code)
                throw new DatabaseFailedException("Database insertion failed: movie, hall and date can not be null.");
            else if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY.code)
                throw new DatabaseFailedException("Database insertion failed: be sure that both the movie and hall have a valid ids.");
            else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(@NotNull ShowTime showTime) throws DatabaseFailedException {
        try{
            if(!dao.update(showTime.getId(), showTime.getMovie().getId(), showTime.getHall().getId()))
                throw new DatabaseFailedException("Update failed.");
        } catch (SQLiteException e){
            if(e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code)
                throw new DatabaseFailedException("Database update failed: this showtime already exists.");
            else if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL.code)
                throw new DatabaseFailedException("Database update failed: movie, hall and date can not be null.");
            else if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY.code)
                throw new DatabaseFailedException("Database update failed: be sure that both the movie and hall have a valid ids.");
            else throw new RuntimeException(e); // TODO throw it as DatabaseInsertionFailedException
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(@NotNull ShowTime showTime) throws DatabaseFailedException {
        try {
            if(!dao.delete(showTime.getId()))
                throw new DatabaseFailedException("Deletion failed.");
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public ShowTime get(int showTimeId) {
        try(ResultSet res = dao.get(showTimeId)){
            if(res.next())
                return new ShowTime(res);
            return null;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ShowTime> get(@NotNull Movie movie) {
        try(ResultSet res = dao.get(movie)) {
            return getList(res, (showTimeList) -> {
                Hall hall = HallFactory.createHall(res);
                ShowTime showTime = new ShowTime(res);
                showTime.setMovie(movie);
                showTime.setHall(hall);
                showTime.setDate(LocalDateTime.parse(res.getString(4), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                return showTime;
            });
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
