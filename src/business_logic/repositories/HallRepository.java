package business_logic.repositories;

import business_logic.Observer;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.DatabaseEntity;
import domain.Hall;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.function.Consumer;

public interface HallRepository extends Observer<DatabaseEntity> {

    void insert(Hall hall) throws DatabaseFailedException, InvalidIdException;

    void update(@NotNull Hall hall, @NotNull Consumer<Hall> apply) throws DatabaseFailedException, InvalidIdException;

    void delete(@NotNull Hall hall) throws DatabaseFailedException, InvalidIdException;

    Hall get(@NotNull ShowTime showTime) throws InvalidIdException, DatabaseFailedException;

    Hall get(Hall hall) throws InvalidIdException;

    HashMap<Integer, WeakReference<Hall>> getEntities();

}
