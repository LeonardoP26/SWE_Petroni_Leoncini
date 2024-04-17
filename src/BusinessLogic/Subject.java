package BusinessLogic;

import java.sql.SQLException;
import java.util.ArrayList;

public abstract class Subject {

    private final ArrayList<Observer> observers = new ArrayList<>();

    protected void addObserver(Observer observer){
        observers.add(observer);
    }

    protected void notifyObservers(Subject subject) throws SQLException {
        for(Observer observer : observers){
            observer.update(subject);
        }
    }

}
