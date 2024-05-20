package business_logic;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import org.jetbrains.annotations.NotNull;

public interface Observer<T> {

    void update(@NotNull T entity) throws DatabaseFailedException, InvalidIdException;

}
