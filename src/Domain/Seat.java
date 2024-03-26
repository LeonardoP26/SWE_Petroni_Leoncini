package Domain;

import BusinessLogic.Subject;
import BusinessLogic.UnableToOpenDatabaseException;
import BusinessLogic.repositories.HallRepository;
import BusinessLogic.repositories.HallRepositoryInterface;
import BusinessLogic.repositories.SeatsRepository;

import java.sql.SQLException;

public class Seat extends Subject {

    private final int id;
    private final int hallId;
    private boolean isBooked;
    private final char row;
    private final int number;
    private final HallRepositoryInterface hallRepo = HallRepository.getInstance();

    public Seat(int id, char row, int number, int hallId, boolean isBooked){
        this(id, row, number, hallId);
        this.isBooked = isBooked;
    }

    public Seat(int id, char row, int number, int hallId){
        this.id = id;
        this.hallId = hallId;
        this.number = number;
        this.row = row;
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
