package business_logic;

import org.jetbrains.annotations.NotNull;

public interface Observer<T> {

    void update(@NotNull T entity);

}
