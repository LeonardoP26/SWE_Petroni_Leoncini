package utils;

@FunctionalInterface
public interface ThrowingSupplier<R> {

    R get();

}
