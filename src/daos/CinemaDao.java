package daos;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Cinema;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CinemaDao extends Dao{

    void insert(@NotNull Cinema cinema) throws DatabaseFailedException;

    void update(@NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException;

    void delete(@NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException;

    List<Cinema> get();

}
