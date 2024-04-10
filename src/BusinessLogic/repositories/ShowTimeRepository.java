package BusinessLogic.repositories;

import BusinessLogic.HallFactory;
import BusinessLogic.exceptions.DatabaseInsertionFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Hall;
import Domain.Movie;
import Domain.Seat;
import Domain.ShowTime;
import daos.ShowTimeDao;
import daos.ShowTimeDaoInterface;
import org.jetbrains.annotations.NotNull;

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
    public int insert(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException {
        try(ResultSet res = dao.insert(showTime.getMovie().getId(), showTime.getHall().getId(), showTime.getDate())){
            if(res.next())
                return res.getInt(1);
            throw new DatabaseInsertionFailedException("Database insertion failed");
        }
    }

    @Override
    public boolean update(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException {
        return dao.update(showTime.getId(), showTime.getMovie().getId(), showTime.getHall().getId());
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
                Hall hall = HallFactory.crateHall(res);
                ShowTime showTime = new ShowTime(res);
                showTime.setMovie(movie);
                showTime.setHall(hall);
                showTime.setDate(LocalDateTime.parse(res.getString(4), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                return showTime;
            });
        }
    }

    @Override
    public void insertShowTimeSeat(int showTimeId, int seatId) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException {
        if(!dao.insertShowTimeSeat(showTimeId, seatId))
            throw new DatabaseInsertionFailedException("Database insertion failed");
    }

    @Override
    public boolean updateShowTimeSeat(ShowTime showTime, Seat seat, int bookingNumber) throws SQLException, UnableToOpenDatabaseException {
        return dao.updateShowTimeSeat(showTime.getId(), seat.getId(), bookingNumber);
    }

}
