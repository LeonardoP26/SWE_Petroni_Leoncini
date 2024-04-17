package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import Domain.Hall;
import Domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public interface HallRepositoryInterface {


    int insert(@NotNull Hall hall, int cinemaId) throws DatabaseFailedException;

    boolean update(@NotNull Hall hall, int cinemaId) throws DatabaseFailedException;

    boolean delete(@NotNull Hall hall);

    Hall get(int hallId);

    Hall get(@NotNull ShowTime showTime);
}
