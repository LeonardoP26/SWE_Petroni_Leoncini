package unit_test.repositories.fake_repositories;

import business_logic.repositories.ShowTimeRepository;
import domain.Cinema;
import domain.DatabaseEntity;
import domain.Movie;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class FakeShowTimeRepository implements ShowTimeRepository {

    @Override
    public void insert(@NotNull ShowTime showTime) {

    }

    @Override
    public void update(@NotNull ShowTime showTime, @NotNull Consumer<ShowTime> edits) {

    }

    @Override
    public void delete(@NotNull ShowTime showTime) {

    }

    @Override
    public List<ShowTime> get(@NotNull Movie movie, @NotNull Cinema cinema) {
        return List.of();
    }

    @Override
    public HashMap<Integer, WeakReference<ShowTime>> getEntities() {
        return null;
    }

    @Override
    public ShowTime get(ShowTime showTime) {
        return null;
    }

    @Override
    public void update(@NotNull DatabaseEntity entity) {

    }
}
