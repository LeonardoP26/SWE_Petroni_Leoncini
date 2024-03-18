package Domain;

import java.util.ArrayList;
import java.util.List;

public class SeatsRow {
    private List<Seat> seats;

    public List<Seat> getSeats() {
        return seats;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    private int rowNumber;
    private int numberOfSeats;

    public SeatsRow(int rowNumber, int numberOfSeats, boolean isVip, boolean isBooked) {
        this.rowNumber = rowNumber;
        seats = new ArrayList<>(numberOfSeats);
        for (int i = 0; i < numberOfSeats; i++) {
            seats.add(new Seat(i, isVip, isBooked));
        }
    }



}
