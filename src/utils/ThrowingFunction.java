package utils;

@FunctionalInterface
public interface ThrowingFunction<P, R> {

    R apply(P p) throws Exception;

}
