package domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Hall implements DatabaseEntity {

    protected int id = ENTITY_WITHOUT_ID;
    protected final int cost = 10;
    private final HallTypes type = HallTypes.STANDARD;
    private Cinema cinema;
    protected ArrayList<Seat> seats = new ArrayList<>();
    protected int hallNumber;

    public enum HallTypes {
        STANDARD, IMAX, THREE_D, IMAX_3D
    }

    public Hall(@NotNull ResultSet res) throws SQLException {
        this.id = res.getInt("hall_id");
    }

    public Hall(@NotNull Hall hall) {
        this.hallNumber = hall.getHallNumber();
        this.cinema = hall.getCinema();
    }

    public Hall(int hallNumber, Cinema cinema) {
        this.hallNumber = hallNumber;
        this.cinema = cinema;
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

    public void setHallNumber(int hallNumber){
        this.hallNumber = hallNumber;
    }

    public ArrayList<Seat> getSeats() {
        return seats;
    }

    public void setSeats(@Nullable ArrayList<Seat> seats){
        this.seats = seats;
    }

    public void setId(@NotNull ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("hall_id");
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    @Override
    public int getId() {
        return id;
    }

    public void copy(@NotNull Hall hall){
        this.setHallNumber(hall.getHallNumber());
        this.setCinema(hall.getCinema());
    }

    @Override
    public void resetId() {
        this.id = ENTITY_WITHOUT_ID;
    }
}
