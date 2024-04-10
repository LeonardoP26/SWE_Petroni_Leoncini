package Domain;

import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import BusinessLogic.repositories.HallRepository;
import BusinessLogic.repositories.HallRepositoryInterface;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Hall implements DatabaseEntity {

    public enum HallTypes {
        STANDARD, IMAX, THREE_D, IMAX_3D
    }

    public Hall(ResultSet res) throws SQLException {
        this.id = res.getInt("hallId");
        this.hallNumber = res.getInt("hallNumber");
    }

    public Hall(int hallNumber) {
        this.hallNumber = hallNumber;
    }

    protected int id = ENTITY_WITHOUT_ID;
    protected final int cost = 10;
    private final HallTypes type = HallTypes.STANDARD;
    protected List<Seat> seats = null;
    protected int hallNumber;

    public int getId() {
        return id;
    }

    @Override
    public String getName(){
        return String.valueOf(hallNumber);
    }



    public int getCost() {
        return cost;
    }

    public HallTypes getHallType(){
        return type;
    }

    public int getHallNumber() {
        return hallNumber;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(@NotNull List<Seat> seats){
        this.seats = seats;
    }

    public void setId(int id) {
        this.id = id;
    }

}
