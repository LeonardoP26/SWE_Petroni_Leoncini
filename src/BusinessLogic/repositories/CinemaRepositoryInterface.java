package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseFailedException;
import Domain.Cinema;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface CinemaRepositoryInterface {


    int insert(Cinema cinema) throws DatabaseFailedException;

    boolean update(@NotNull Cinema cinema) throws DatabaseFailedException;

    boolean delete(@NotNull Cinema cinema);

    Cinema get(int cinemaId);

    List<Cinema> get();

}
