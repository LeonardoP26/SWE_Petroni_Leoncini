package domain;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Seat implements DatabaseEntity {

    private int id = ENTITY_WITHOUT_ID;
    private boolean isBooked;
    private char row;
    private int number;

    public Seat(ResultSet res) throws SQLException {
        this.id = res.getInt("seat_id");
    }

    public Seat(char row, int number){
        this.number = number;
        this.row = row;
    }

    public Seat(@NotNull Seat seat){
        this.row = seat.getRow();
        this.number = seat.getNumber();
        this.isBooked = seat.isBooked();
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean isBooked) {
        this.isBooked = isBooked;
    }

    @Override
    public String getName(){
        return String.valueOf(row + number);
    }

    public char getRow() {
        return row;
    }

    public void setRow(char row) {
        this.row = row;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(@NotNull ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("seat_id");
    }

    @Override
    public void resetId() {
        this.id = ENTITY_WITHOUT_ID;
    }

    public void copy(@NotNull Seat seat){
        this.row = seat.getRow();
        this.number = seat.getNumber();
        this.isBooked = seat.isBooked();
    }

}
