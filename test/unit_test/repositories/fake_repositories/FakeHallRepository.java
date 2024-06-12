package unit_test.repositories.fake_repositories;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.repositories.HallRepository;
import domain.DatabaseEntity;
import domain.Hall;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.function.Consumer;

public class FakeHallRepository implements HallRepository {

    @Override
    public void insert(Hall hall) throws DatabaseFailedException, InvalidIdException {

    }

    @Override
    public void update(@NotNull Hall hall, @NotNull Consumer<Hall> apply) throws DatabaseFailedException, InvalidIdException {

    }

    @Override
    public void delete(@NotNull Hall hall) throws DatabaseFailedException, InvalidIdException {

    }

    @Override
    public Hall get(@NotNull ShowTime showTime) throws InvalidIdException, DatabaseFailedException {
        return null;
    }

    @Override
    public Hall get(Hall hall) throws InvalidIdException {
        return null;
    }

    @Override
    public HashMap<Integer, WeakReference<Hall>> getEntities() {
        return null;
    }

    @Override
    public void update(@NotNull DatabaseEntity entity) throws DatabaseFailedException, InvalidIdException {

    }
}
