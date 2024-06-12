package unit_test.repositories.fake_repositories;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.repositories.CinemaRepository;
import domain.Cinema;
import domain.DatabaseEntity;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class FakeCinemaRepository implements CinemaRepository {

    @Override
    public void insert(@NotNull Cinema cinema) throws DatabaseFailedException {

    }

    @Override
    public void update(@NotNull Cinema cinema, @NotNull Consumer<Cinema> edits) throws DatabaseFailedException, InvalidIdException {

    }

    @Override
    public void delete(@NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException {

    }

    @Override
    public List<Cinema> get() {
        return List.of();
    }

    @Override
    public Cinema get(Cinema cinema) throws InvalidIdException {
        return null;
    }

    @Override
    public WeakHashMap<Integer, WeakReference<Cinema>> getEntities() {
        return null;
    }

    @Override
    public void update(@NotNull DatabaseEntity entity) throws DatabaseFailedException, InvalidIdException {

    }
}
