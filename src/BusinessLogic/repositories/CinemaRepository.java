package BusinessLogic.repositories;

import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Cinema;
import Domain.Hall;
import daos.CinemaDao;
import daos.CinemaDaoInterface;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CinemaRepository implements CinemaRepositoryInterface {

    private final CinemaDaoInterface dao = CinemaDao.getInstance();

    private static CinemaRepositoryInterface instance = null;
    public static CinemaRepositoryInterface getInstance(){
        if(instance == null)
            instance = new CinemaRepository();
        return instance;
    }

    private CinemaRepository() { }

    @Override
    public void insert(Cinema cinema) throws SQLException, UnableToOpenDatabaseException {
        dao.insert(cinema);
    }

    @Override
    public List<Hall> getCinemaHalls(Cinema cinema) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.getCinemaHalls(cinema)){
            if(!res.isBeforeFirst())
                return null;
            List<Hall> halls = new ArrayList<>();
            while(res.next()){
                Hall hall = new Hall(res.getInt(1), res.getInt(2));
                halls.add(hall);
            }
            return halls;
        }
    }

}
