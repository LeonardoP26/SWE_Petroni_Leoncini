package BusinessLogic.repositories;

import BusinessLogic.Observer;
import BusinessLogic.Subject;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Seat;

import java.sql.SQLException;

public interface SeatsRepositoryInterface extends Observer {

    Seat getSeat(Seat seat) throws SQLException, UnableToOpenDatabaseException;

}
