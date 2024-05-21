package daos;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Cinema;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CinemaDao extends Dao{

    void insert(@NotNull Cinema cinema) throws DatabaseFailedException;

    void update(@NotNull Cinema cinema, @NotNull Cinema copy) throws DatabaseFailedException;

    void delete(@NotNull Cinema cinema) throws DatabaseFailedException;

    List<Cinema> get();

    Cinema get(Cinema cinema);
}
