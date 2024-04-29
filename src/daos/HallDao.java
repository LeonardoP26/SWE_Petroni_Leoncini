package daos;

import business_logic.exceptions.DatabaseFailedException;
import domain.Hall;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

public interface HallDao extends Dao {

    void insert(@NotNull Hall hall, int cinemaId) throws DatabaseFailedException;

    void update(@NotNull Hall hall, int cinemaId) throws DatabaseFailedException;

    void delete(@NotNull Hall hall) throws DatabaseFailedException;

    Hall get(int hallId);

    Hall get(@NotNull ShowTime showTime);
}
