package unit_test.repositories.fake_repositories;

import business_logic.repositories.CinemaRepository;
import domain.Cinema;
import domain.DatabaseEntity;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class FakeCinemaRepository implements CinemaRepository {

    @Override
    public void insert(@NotNull Cinema cinema) {

    }

    @Override
    public void update(@NotNull Cinema cinema, @NotNull Consumer<Cinema> edits) {

    }

    @Override
    public void delete(@NotNull Cinema cinema) {

    }

    @Override
    public List<Cinema> get() {
        return List.of();
    }

    @Override
    public Cinema get(Cinema cinema) {
        return null;
    }

    @Override
    public HashMap<Integer, WeakReference<Cinema>> getEntities() {
        return null;
    }

    @Override
    public void update(@NotNull DatabaseEntity entity) {

    }
}
