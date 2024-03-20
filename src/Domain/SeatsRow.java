package Domain;

import java.util.ArrayList;

public class SeatsRow {


    public SeatsRow(char row, int numSeats){
        this.row = row;
        seats = new ArrayList<>(numSeats);
        for (int i = 1; i <= numSeats; i++){
            seats.add(new Seat(row, i));
        }
    }

    private char row;
    private ArrayList<Seat> seats;

    public char getRow() {
        return row;
    }

    public ArrayList<Seat> getSeats() {
        return seats;
    }

}
