package Domain;

public class Seat {

    public Seat(char row, int number){
        this.number = number;
        this.row = row;
    }

    public Seat(char row, int number, boolean isBooked){
        this.number = number;
        this.row = row;
        this.isBooked = isBooked;
    }

    private boolean isBooked = false;
    private char row;
    private int number;

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public char getRow() {
        return row;
    }

    public int getNumber() {
        return number;
    }
}
