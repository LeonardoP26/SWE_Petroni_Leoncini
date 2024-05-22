package business_logic.repositories;

import business_logic.Observer;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Cinema;
import domain.DatabaseEntity;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public interface CinemaRepository extends Observer<DatabaseEntity>  {


    void insert(@NotNull Cinema cinema) throws DatabaseFailedException;

    void update(@NotNull Cinema cinema, @NotNull Consumer<Cinema> edits) throws DatabaseFailedException, InvalidIdException;

    void delete(@NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException;

    List<Cinema> get();

    Cinema get(Cinema cinema) throws InvalidIdException;

    WeakHashMap<Integer, WeakReference<Cinema>> getEntities();
}
