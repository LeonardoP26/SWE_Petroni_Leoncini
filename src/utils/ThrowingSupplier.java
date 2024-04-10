package utils;

import java.sql.SQLException;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Exception> {

    T get() throws E;

}
