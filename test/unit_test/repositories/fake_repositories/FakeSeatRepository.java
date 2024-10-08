package unit_test.repositories.fake_repositories;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.repositories.SeatRepository;
import domain.DatabaseEntity;
import domain.Hall;
import domain.Seat;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class FakeSeatRepository implements SeatRepository {

    @Override
    public void insert(@NotNull Seat seat, @NotNull Hall hall) {

    }

    @Override
    public void update(@NotNull Seat seat, @NotNull Hall hall, Consumer<Seat> edits) {

    }

    @Override
    public void delete(@NotNull Seat seat, @NotNull Hall hall) {

    }

    @Override
    public List<Seat> get(@NotNull ShowTime showTime) {
        return List.of();
    }

    @Override
    public Seat get(Seat seat) {
        return null;
    }

    @Override
    public HashMap<Integer, WeakReference<Seat>> getEntities() {
        return null;
    }

    @Override
    public void update(@NotNull DatabaseEntity entity) {

    }
}
