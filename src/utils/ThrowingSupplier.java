package utils;

@FunctionalInterface
public interface ThrowingSupplier<R, E extends Exception> {

    R get() throws E;

}
