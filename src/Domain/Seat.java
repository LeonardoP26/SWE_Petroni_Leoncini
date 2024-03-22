package Domain;

public class Seat {

    private int id;
    private Hall hall;
    private boolean isBooked = false;
    private char row;
    private int number;

    public Seat(int id, char row, int number, Hall hall){
        this.id = id;
        this.number = number;
        this.row = row;
        this.hall = hall;
    }

    public Seat(int id, char row, int number, Hall hall, boolean isBooked){
        this.id = id;
        this.hall = hall;
        this.number = number;
        this.row = row;
        this.isBooked = isBooked;
    }

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

    public Hall getHall(){
        return hall;
    }

    public int getId() {
        return id;
    }
}
