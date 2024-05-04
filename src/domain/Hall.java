package domain;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Hall implements DatabaseEntity {

    public enum HallTypes {
        STANDARD, IMAX, THREE_D, IMAX_3D
    }

    public Hall(ResultSet res) throws SQLException {
        this.id = res.getInt("hall_id");
        this.hallNumber = res.getInt("hall_number");
    }

    public Hall(int hallNumber) {
        this.hallNumber = hallNumber;
    }

    protected int id = ENTITY_WITHOUT_ID;
    protected final int cost = 10;
    private final HallTypes type = HallTypes.STANDARD;
    protected ArrayList<Seat> seats = null;
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

    public ArrayList<Seat> getSeats() {
        return seats;
    }

    public void setSeats(@NotNull ArrayList<Seat> seats){
        this.seats = seats;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId(@NotNull ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("hall_id");
    }

}
