package Domain;

import BusinessLogic.Subject;
import BusinessLogic.UnableToOpenDatabaseException;
import BusinessLogic.repositories.HallRepository;
import BusinessLogic.repositories.HallRepositoryInterface;
import BusinessLogic.repositories.SeatsRepository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Seat extends Subject implements DatabaseEntity {

    private final int id;
    private final int hallId;
    private boolean isBooked;
    private final char row;
    private final int number;
    private final HallRepositoryInterface hallRepo = HallRepository.getInstance();

    public Seat(ResultSet res) throws SQLException {
        this(res.getInt(1), res.getString(2).charAt(0), res.getInt(3), res.getInt(4));
        this.isBooked = res.getBoolean(5);
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

    @Override
    public int getId() {
        return id;
    }
}
