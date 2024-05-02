package business_logic;

import business_logic.exceptions.DatabaseFailedException;

public interface Observer<T> {

    void update(T entity);

}
