package Domain;

import BusinessLogic.UnableToOpenDatabaseException;
import BusinessLogic.repositories.HallRepository;
import BusinessLogic.repositories.HallRepositoryInterface;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Hall implements DatabaseEntity {

    public enum HallTypes {
        STANDARD, IMAX, THREE_D, IMAX_3D
    }

    public Hall(ResultSet res) throws SQLException {
        this(res.getInt(1), res.getInt(2));
    }

    public Hall(int id, int cinemaId){
        this.id = id;
        this.cinemaId = cinemaId;
    }

    private final int id;
    private final int cinemaId;
    protected final int cost = 10;
    private final HallTypes type = HallTypes.STANDARD;

    private final HallRepositoryInterface hallRepo = HallRepository.getInstance();

    @Override
    public int getId() {
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

    public List<Seat> getSeats() throws SQLException, UnableToOpenDatabaseException {
        return hallRepo.getHallSeats(this);
    }

}
