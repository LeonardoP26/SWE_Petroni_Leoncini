package daos;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Cinema;
import domain.Hall;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

public interface HallDao extends Dao {

    void insert(@NotNull Hall hall) throws DatabaseFailedException;

    void update(@NotNull Hall hall, @NotNull Hall copy) throws DatabaseFailedException;

    void delete(@NotNull Hall hall) throws DatabaseFailedException;

    Hall get(@NotNull ShowTime showTime);

    Hall get(Hall hall);
}
