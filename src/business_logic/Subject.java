package business_logic;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject<T> {

    private final List<Observer<T>> observers = new ArrayList<>();

    protected void addObserver(Observer<T> observer){
        observers.add(observer);
    }

    protected void notifyObservers(T entity) throws DatabaseFailedException, InvalidIdException {
        for(Observer<T> observer: observers) {
            observer.update(entity);
        }
    }

}
