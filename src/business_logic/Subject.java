package business_logic;

import business_logic.exceptions.DatabaseFailedException;
import domain.DatabaseEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class Subject<T> {

    private final List<Observer<T>> observers = new ArrayList<>();

    protected void addObserver(Observer<T> observer){
        observers.add(observer);
    }

    protected void notifyObservers(T entity) {
        for(Observer<T> observer: observers){
            observer.update(entity);
        }
    }

}
