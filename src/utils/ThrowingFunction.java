package utils;

import java.sql.SQLException;

@FunctionalInterface
public interface ThrowingFunction<P, R, E extends Exception> {

    R apply(P p) throws E, SQLException;

}
