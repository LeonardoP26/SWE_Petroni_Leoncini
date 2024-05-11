package business_logic.repositories;

import business_logic.CinemaDatabase;
import business_logic.Subject;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import daos.HallDao;
import daos.HallDaoImpl;
import domain.Cinema;
import domain.DatabaseEntity;
import domain.Hall;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.function.Consumer;

public class HallRepositoryImpl extends Subject<DatabaseEntity> implements HallRepository {

    private static WeakReference<HallRepository> instance = null;
    private final HashMap<Integer, WeakReference<Hall>> entities = new HashMap<>();
    private final HallDao hallDao;

    private HallRepositoryImpl(HallDao hallDao, SeatRepository seatRepo, ShowTimeRepository showTimeRepo){
        this.hallDao = hallDao;
        addObserver(seatRepo);
        addObserver(showTimeRepo);
    }

    public static @NotNull HallRepository getInstance(){
        return getInstance(
                HallDaoImpl.getInstance(CinemaDatabase.DB_URL),
                SeatRepositoryImpl.getInstance(),
                ShowTimeRepositoryImpl.getInstance()
        );
    }

    public static @NotNull HallRepository getInstance(@NotNull HallDao hallDao, @NotNull SeatRepository seatRepo, @NotNull ShowTimeRepository showTimeRepo) {
        HallRepository inst = instance != null ? instance.get() : null;
        if(inst != null)
            return inst;
        inst = new HallRepositoryImpl(hallDao, seatRepo, showTimeRepo);
        instance = new WeakReference<>(inst);
        return inst;
    }

    @Override
    public void insert(Hall hall, Cinema cinema) throws DatabaseFailedException, InvalidIdException {
        hallDao.insert(hall ,cinema);
        entities.put(hall.getId(), new WeakReference<>(hall));
        cinema.getHalls().add(hall);
    }

    @Override
    public void update(@NotNull Hall hall, @NotNull Cinema cinema, @NotNull Consumer<Hall> apply) throws DatabaseFailedException, InvalidIdException {
        Hall copy = new Hall(hall);
        apply.accept(copy);
        hallDao.update(hall, copy, cinema);
        hall.copy(copy);
    }

    @Override
    public void delete(@NotNull Hall hall, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException {
        hallDao.delete(hall);
        notifyObservers(hall);
        hall.resetId();
    }

    @Override
    public Hall get(@NotNull ShowTime showTime) throws InvalidIdException {
        Hall hall = hallDao.get(showTime);
        Hall cached = entities.get(hall.getId()) != null ? entities.get(hall.getId()).get() : null;
        if(cached == null) {
            entities.put(hall.getId(), new WeakReference<>(hall));
            return hall;
        }
        // TODO Seats are not tracked
        cached.copy(hall);
        return cached;
    }


    @Override
    public void update(@NotNull DatabaseEntity entity) {
        if(entity instanceof Cinema) {
            ((Cinema) entity).getHalls().forEach( h -> {
                notifyObservers(h);
                entities.remove(h.getId());
                h.resetId();
            });
        }
    }

    @Override
    public HashMap<Integer, WeakReference<Hall>> getEntities() {
        return entities;
    }



}
