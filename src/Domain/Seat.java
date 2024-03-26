package Domain;

import BusinessLogic.Subject;
import BusinessLogic.UnableToOpenDatabaseException;
import BusinessLogic.repositories.HallRepository;
import BusinessLogic.repositories.HallRepositoryInterface;
import BusinessLogic.repositories.SeatsRepository;

import java.sql.SQLException;

public class Seat extends Subject {

    private int id;
    private int hallId;
    private boolean isBooked = false;
    private char row;
    private int number;
    private HallRepositoryInterface hallRepo = HallRepository.getInstance();

    public Seat(int id, char row, int number, int hallId, boolean isBooked){
        this.id = id;
        this.hallId = hallId;
        this.number = number;
        this.row = row;
        this.isBooked = isBooked;
        addObserver(SeatsRepository.getInstance());
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) throws SQLException, UnableToOpenDatabaseException {
        isBooked = booked;
        notifyObservers(this);
    }

    public char getRow() {
        return row;
    }

    public int getNumber() {
        return number;
    }

    public int getHallId(){
        return hallId;
    }

    public Hall getHall() throws SQLException, UnableToOpenDatabaseException {
        return hallRepo.getHall(hallId);
    }

    public int getId() {
        return id;
    }
}
