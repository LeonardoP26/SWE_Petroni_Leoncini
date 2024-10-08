package business_logic.repositories;

import business_logic.Observer;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Cinema;
import domain.DatabaseEntity;
import domain.Movie;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public interface ShowTimeRepository extends Observer<DatabaseEntity> {


    void insert(@NotNull ShowTime showTime) throws DatabaseFailedException, InvalidIdException;

    void update(@NotNull ShowTime showTime, @NotNull Consumer<ShowTime> edits) throws DatabaseFailedException, InvalidIdException;

    void delete(@NotNull ShowTime showTime) throws DatabaseFailedException, InvalidIdException;

    List<ShowTime> get(@NotNull Movie movie, @NotNull Cinema cinema) throws InvalidIdException;

    HashMap<Integer, WeakReference<ShowTime>> getEntities();

    ShowTime get(ShowTime showTime) throws InvalidIdException;
}
