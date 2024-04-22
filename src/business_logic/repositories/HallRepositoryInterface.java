package business_logic.repositories;

import business_logic.exceptions.DatabaseFailedException;
import domain.Hall;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

public interface HallRepositoryInterface {


    void insert(@NotNull Hall hall, int cinemaId) throws DatabaseFailedException;

    void update(@NotNull Hall hall, int cinemaId) throws DatabaseFailedException;

    void delete(@NotNull Hall hall) throws DatabaseFailedException;

    Hall get(int hallId);

    Hall get(@NotNull ShowTime showTime);
}
