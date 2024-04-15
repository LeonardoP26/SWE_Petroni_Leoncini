package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseInsertionFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Movie;
import Domain.Seat;
import Domain.ShowTime;
import daos.SeatsDao;
import daos.SeatsDaoInterface;
import org.jetbrains.annotations.NotNull;

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
    public int insert(Seat seat, int hallId) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException {
        try(ResultSet res = dao.insert(seat.getRow(), seat.getNumber(), hallId)){
            if(res.next())
                return res.getInt(1);
            throw new DatabaseInsertionFailedException("Database insertion failed: Record already present");
        }
    }

    @Override
    public boolean update(@NotNull Seat seat, int hallId) throws SQLException, UnableToOpenDatabaseException {
        return dao.update(seat.getId(), seat.getRow(), seat.getNumber(), hallId);
    }

    @Override
    public boolean delete(@NotNull Seat seat) throws SQLException, UnableToOpenDatabaseException {
        return dao.delete(seat.getId());
    }

    @Override
    public Movie get(int seatId) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.get(seatId)){
            if(res.next())
                return new Movie(res);
            return null;
        }
    }

    @Override
    public List<Seat> get(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.get(showTime)){
            return getList(res, () -> {
                Seat seat = new Seat(res);
                seat.setBooked(res.getInt("booking_number") > 0);
                return seat;
            });
        }
    }

}
