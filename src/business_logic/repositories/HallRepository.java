package business_logic.repositories;

import business_logic.Observer;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import domain.Cinema;
import domain.DatabaseEntity;
import domain.Hall;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.function.Consumer;

public interface HallRepository extends Observer<DatabaseEntity> {

    void insert(Hall hall, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException;

    void update(@NotNull Hall hall, @NotNull Cinema cinema, @NotNull Consumer<Hall> apply) throws DatabaseFailedException, InvalidIdException;

    void delete(@NotNull Hall hall, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException;

    Hall get(@NotNull ShowTime showTime, @NotNull Cinema cinema) throws InvalidIdException, DatabaseFailedException;

    HashMap<Integer, WeakReference<Hall>> getEntities();

}
