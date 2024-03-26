package BusinessLogic.repositories;

import BusinessLogic.UnableToOpenDatabaseException;
import Domain.Hall;
import Domain.ShowTime;

import java.sql.SQLException;
import java.util.List;

public interface HallRepositoryInterface {


    Hall getHall(int hallId) throws SQLException, UnableToOpenDatabaseException;

    List<ShowTime> getHallShowTimes(Hall hall) throws SQLException, UnableToOpenDatabaseException;
}
