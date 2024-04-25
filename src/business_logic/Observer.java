package business_logic;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.UserAlreadyExistsException;

public interface Observer<T> {

    void update(T entity) throws DatabaseFailedException;

}
