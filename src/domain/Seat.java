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
        this.row = res.getString("row").charAt(0);
        this.number = res.getInt("number");
    }

    public Seat(char row, int number){
        this.number = number;
        this.row = row;
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

    public int getNumber() {
        return number;
    }

    public int getId() {
        return id;
    }

    public void setId(@NotNull ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("seat_id");
    }

    public void setId(int id) {
        this.id = id;
    }
}
