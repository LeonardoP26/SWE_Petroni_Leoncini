package BusinessLogic.repositories;

import BusinessLogic.HallFactory;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Hall;
import Domain.Movie;
import Domain.ShowTime;
import daos.HallDao;
import daos.HallDaoInterface;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
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
            return HallFactory.crateHall(res.getInt(1), res.getInt(2), res.getString(3));
        }
    }

    @Override
    public List<ShowTime> getHallShowTimes(Hall hall) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.getHallMovies(hall)){
            if(!res.isBeforeFirst())
                return null;
            List<ShowTime> showTimes = new ArrayList<>();
            while(res.next()){
                ShowTime showTime = new ShowTime(res.getInt(1), res.getInt(2), res.getInt(3), res.getString(4));
                showTimes.add(showTime);
            }
            return showTimes;
        }
    }


}
