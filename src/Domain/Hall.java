package Domain;

import BusinessLogic.UnableToOpenDatabaseException;
import BusinessLogic.repositories.HallRepository;
import BusinessLogic.repositories.HallRepositoryInterface;

import java.sql.SQLException;
import java.util.List;

import static java.lang.Math.abs;

public class Hall{

    public enum HallTypes {
        STANDARD, IMAX, THREE_D, IMAX_3D
    }


    public Hall(int id, int cinemaId){
        this.id = id;
        this.cinemaId = cinemaId;
    }

    private final int id;
    private final int cinemaId;
    protected int cost = 10;
    private final HallTypes type = HallTypes.STANDARD;

    private final HallRepositoryInterface hallRepo = HallRepository.getInstance();

    public Integer getId() {
        return id;
    }

    public int getCinemaId() {
        return cinemaId;
    }

    public int getCost() {
        return cost;
    }

    public HallTypes getHallType(){
        return type;
    }

    public List<ShowTime> getShowTimes() throws SQLException, UnableToOpenDatabaseException {
        return hallRepo.getHallShowTimes(this);
    }


}
