package utils;

public interface ThrowingConsumer<P> {

    void accept(P p) throws Exception;

}
