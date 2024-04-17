package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import Domain.Movie;
import Domain.Seat;
import Domain.ShowTime;
import daos.SeatsDao;
import daos.SeatsDaoInterface;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SeatsRepository extends Repository implements SeatsRepositoryInterface {

    private final SeatsDaoInterface dao = SeatsDao.getInstance();
    private static SeatsRepositoryInterface instance = null;

    public static SeatsRepositoryInterface getInstance() {
        if (instance == null)
            instance = new SeatsRepository();
        return instance;
    }

    private SeatsRepository() { }


    @Override
    public int insert(Seat seat, int hallId) throws DatabaseFailedException {
        try(ResultSet res = dao.insert(seat.getRow(), seat.getNumber(), hallId)){
            if(res.next())
                return res.getInt(1);
            throw new DatabaseFailedException("Database insertion failed.");
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
    public boolean update(@NotNull Seat seat, int hallId) throws DatabaseFailedException {
        try{
            return dao.update(seat.getId(), seat.getRow(), seat.getNumber(), hallId);
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
    public boolean delete(@NotNull Seat seat) {
        try{
            return dao.delete(seat.getId());
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Movie get(int seatId) {
        try(ResultSet res = dao.get(seatId)){
            if(res.next())
                return new Movie(res);
            return null;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Seat> get(@NotNull ShowTime showTime) {
        try(ResultSet res = dao.get(showTime)){
            return getList(res, (seatList) -> {
                Seat seat = new Seat(res);
                seat.setBooked(res.getInt("booking_number") > 0);
                return seat;
            });
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

}
