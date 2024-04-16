package BusinessLogic.repositories;

import BusinessLogic.HallFactory;
import BusinessLogic.exceptions.DatabaseFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Hall;
import Domain.Movie;
import Domain.Seat;
import Domain.ShowTime;
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
    public int insert(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException {
        try(ResultSet res = dao.insert(showTime.getMovie().getId(), showTime.getHall().getId(), showTime.getDate())){
            if(res.next())
                return res.getInt(1);
            throw new DatabaseFailedException("Database insertion failed.");
        } catch (SQLException e){
            if(e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code)
                throw new DatabaseFailedException("Database insertion failed: this showtime already exists.");
            else if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL.code)
                throw new DatabaseFailedException("Database insertion failed: movie, hall and date can not be null.");
            else if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY.code)
                throw new DatabaseFailedException("Database insertion failed: be sure that both the movie and hall have a valid ids.");
            else throw e; // TODO throw it as DatabaseInsertionFailedException
        }
    }

    @Override
    public boolean update(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException {
        try{
            return dao.update(showTime.getId(), showTime.getMovie().getId(), showTime.getHall().getId());
        } catch (SQLException e){
            if(e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code)
                throw new DatabaseFailedException("Database update failed: this showtime already exists.");
            else if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL.code)
                throw new DatabaseFailedException("Database update failed: movie, hall and date can not be null.");
            else if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY.code)
                throw new DatabaseFailedException("Database update failed: be sure that both the movie and hall have a valid ids.");
            else throw e; // TODO throw it as DatabaseInsertionFailedException
        }
    }

    @Override
    public boolean delete(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException {
        return dao.delete(showTime.getId());
    }

    @Override
    public ShowTime get(int showTimeId) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.get(showTimeId)){
            if(res.next())
                return new ShowTime(res);
            return null;
        }
    }

    @Override
    public List<ShowTime> get(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.get(movie)) {
            return getList(res, () -> {
                Hall hall = HallFactory.createHall(res);
                ShowTime showTime = new ShowTime(res);
                showTime.setMovie(movie);
                showTime.setHall(hall);
                showTime.setDate(LocalDateTime.parse(res.getString(4), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                return showTime;
            });
        }
    }

    @Override
    public void insertShowTimeSeat(int showTimeId, int seatId) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException {
        try{
            if(!dao.insertShowTimeSeat(showTimeId, seatId))
                throw new DatabaseFailedException("Database insertion failed");
        } catch (SQLiteException e){
            if(e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database insertion failed: this entity already exists."); // TODO maybe use InvalidSeatException?
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database insertion failed: showtime, seat and booking can not be null.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY)
                throw new DatabaseFailedException("Database insertion failed: be sure that both the seat has a valid id and the booking has a valid booking number.");
            else throw e; // TODO throw it as DatabaseInsertionFailedException
        }
    }

    @Override
    public boolean updateShowTimeSeat(ShowTime showTime, Seat seat, int bookingNumber) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException {
        try{
            return dao.updateShowTimeSeat(showTime.getId(), seat.getId(), bookingNumber);
        } catch (SQLiteException e){
            if(e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                throw new DatabaseFailedException("Database update failed: this entity already exists.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL)
                throw new DatabaseFailedException("Database update failed: showtime, seat and booking can not be null.");
            else if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY)
                throw new DatabaseFailedException("Database update failed: be sure that both the seat has a valid id and the booking has a valid booking number.");
            else throw e; // TODO throw it as DatabaseInsertionFailedException
        }
    }

}
