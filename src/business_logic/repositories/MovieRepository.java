package business_logic.repositories;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Cinema;
import domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public interface MovieRepository {

    void insert(@NotNull Movie movie) throws DatabaseFailedException;

    void update(@NotNull Movie movie, @NotNull Cinema cinema, @NotNull Consumer<Movie> edits) throws DatabaseFailedException, InvalidIdException;

    void delete(@NotNull Movie movie) throws DatabaseFailedException, InvalidIdException;

    List<Movie> get(@NotNull Cinema cinema) throws InvalidIdException;

    Movie get(Movie movie) throws InvalidIdException;
}
