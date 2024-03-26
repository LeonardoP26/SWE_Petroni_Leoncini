package BusinessLogic.repositories;

import BusinessLogic.Subject;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Seat;
import daos.SeatsDao;
import daos.SeatsDaoInterface;

import java.sql.SQLException;

public class SeatsRepository implements SeatsRepositoryInterface {

    private final SeatsDaoInterface dao = SeatsDao.getInstance();
    private static SeatsRepositoryInterface instance = null;

    public static SeatsRepositoryInterface getInstance() {
        if (instance == null)
            instance = new SeatsRepository();
        return instance;
    }

    private SeatsRepository() { }

    @Override
    public Seat getSeat(Seat seat) throws SQLException, UnableToOpenDatabaseException {
        return dao.getSeat(seat.getId());
    }

    @Override
    public void update(Subject subject) throws SQLException, UnableToOpenDatabaseException {
        if(subject instanceof Seat)
            dao.update((Seat) subject);
    }

}
