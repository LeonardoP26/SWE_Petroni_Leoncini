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
import java.util.Map;
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
    public void insert(Hall hall) throws DatabaseFailedException, InvalidIdException {
        if(hall.getCinema() == null)
            throw new DatabaseFailedException("Cinema can not be null.");
        if(hall.getCinema().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");
        hallDao.insert(hall, hall.getCinema());
        entities.put(hall.getId(), new WeakReference<>(hall));
    }

    @Override
    public void update(@NotNull Hall hall, @NotNull Consumer<Hall> apply) throws DatabaseFailedException, InvalidIdException {
        if(hall.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        Hall copy = new Hall(hall);
        apply.accept(copy);
        if(copy.getCinema() == null)
            throw new DatabaseFailedException("Cinema can not be null.");
        if(copy.getCinema().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");

        hallDao.update(hall, copy, copy.getCinema());
        hall.copy(copy);
    }

    @Override
    public void delete(@NotNull Hall hall) throws DatabaseFailedException, InvalidIdException {
        if (hall.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        try {
            CinemaDatabase.withTransaction(() -> {
                hallDao.delete(hall);
                notifyObservers(hall);
            });
        } catch (Exception e) {
            if (e instanceof DatabaseFailedException)
                throw (DatabaseFailedException) e;
            if (e instanceof InvalidIdException)
                throw (InvalidIdException) e;
            throw new RuntimeException(e);
        }
        hall.resetId();
    }

    @Override
    public Hall get(@NotNull ShowTime showTime) throws InvalidIdException, DatabaseFailedException {
        if(showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database.");
        Hall hall = hallDao.get(showTime);
        Hall cached = entities.get(hall.getId()) != null ? entities.get(hall.getId()).get() : null;
        return findForCaching(cached);
    }

    @Override
    public Hall get(Hall hall) throws InvalidIdException {
        if(hall.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        return findForCaching(hallDao.get(hall));
    }


    @Override
    public void update(@NotNull DatabaseEntity entity) throws DatabaseFailedException, InvalidIdException {
        if (entity instanceof Cinema) {
            for(Map.Entry<Integer, WeakReference<Hall>> entrySet : entities.entrySet()) {
                Hall h = entrySet.getValue() != null ? entrySet.getValue().get() : null;
                Integer key = entrySet.getKey();
                if(h == null)
                    entities.remove(key);
                else if(h.getCinema() == entity) {
                    notifyObservers(h);
                    entities.remove(key);
                    h.resetId();
                }
            }
        }
    }

    @Override
    public HashMap<Integer, WeakReference<Hall>> getEntities() {
        return entities;
    }

    private Hall findForCaching(Hall hall){
        if(hall == null)
            return null;
        Hall cached = entities.get(hall.getId()) != null ? entities.get(hall.getId()).get() : null;
        if(cached == null) {
            entities.put(hall.getId(), new WeakReference<>(hall));
            return hall;
        }
        cached.copy(hall);
        return cached;
    }

}
