package daos;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Cinema;
import domain.Hall;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

public interface HallDao extends Dao {

    void insert(@NotNull Hall hall, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException;

    void update(@NotNull Hall hall, @NotNull Hall copy, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException;

    void delete(@NotNull Hall hall) throws DatabaseFailedException, InvalidIdException;

    Hall get(@NotNull ShowTime showTime) throws InvalidIdException;
}
