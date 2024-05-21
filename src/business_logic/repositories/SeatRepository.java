package business_logic.repositories;

import business_logic.Observer;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.DatabaseEntity;
import domain.Hall;
import domain.Seat;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public interface SeatRepository extends Observer<DatabaseEntity> {

    void insert(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException;

    void update(@NotNull Seat seat, @NotNull Hall hall, Consumer<Seat> edits) throws InvalidIdException, DatabaseFailedException;

    void delete(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException;

    List<Seat> get(@NotNull ShowTime showTime) throws InvalidIdException;

    Seat get(Seat seat) throws InvalidIdException;

    HashMap<Integer, WeakReference<Seat>> getEntities();
}
