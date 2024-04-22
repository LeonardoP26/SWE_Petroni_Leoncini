package business_logic.repositories;

import business_logic.exceptions.DatabaseFailedException;
import domain.Cinema;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CinemaRepositoryInterface {


    void insert(Cinema cinema) throws DatabaseFailedException;

    void update(@NotNull Cinema cinema) throws DatabaseFailedException;

    void delete(@NotNull Cinema cinema) throws DatabaseFailedException;

    Cinema get(int cinemaId);

    List<Cinema> get();

}
