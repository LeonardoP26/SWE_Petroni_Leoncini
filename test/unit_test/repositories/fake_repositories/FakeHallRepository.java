package unit_test.repositories.fake_repositories;

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
    public void insert(Hall hall) {

    }

    @Override
    public void update(@NotNull Hall hall, @NotNull Consumer<Hall> apply) {

    }

    @Override
    public void delete(@NotNull Hall hall) {

    }

    @Override
    public Hall get(@NotNull ShowTime showTime) {
        return null;
    }

    @Override
    public Hall get(Hall hall) {
        return null;
    }

    @Override
    public HashMap<Integer, WeakReference<Hall>> getEntities() {
        return null;
    }

    @Override
    public void update(@NotNull DatabaseEntity entity) {

    }
}
