package BusinessLogic.repositories;

import BusinessLogic.HallFactory;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Hall;
import Domain.Movie;
import Domain.Seat;
import Domain.ShowTime;
import daos.HallDao;
import daos.HallDaoInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HallRepository implements HallRepositoryInterface{

    private final HallDaoInterface dao = HallDao.getInstance();
    private static HallRepositoryInterface instance = null;

    public static HallRepositoryInterface getInstance(){
        if(instance == null)
            instance = new HallRepository();
        return instance;
    }

    private HallRepository() { }


    @Override
    public Hall getHall(int hallId) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.getHall(hallId)) {
            if (!res.isBeforeFirst())
                return null;
            return HallFactory.crateHall(res);
        }
    }

    @Override
    public List<Seat> getHallSeats(Hall hall) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.getHallSeats(hall)){
            if(!res.isBeforeFirst())
                return null;
            List<Seat> seats = new ArrayList<>();
            while(res.next()){
                Seat seat = new Seat(res);
                seats.add(seat);
            }
            return seats;
        }
    }


}
