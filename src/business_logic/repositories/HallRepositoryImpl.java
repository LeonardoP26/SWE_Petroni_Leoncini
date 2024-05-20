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

import javax.xml.crypto.Data;
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
    public void insert(Hall hall, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException {
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");
        hallDao.insert(hall, cinema);
        entities.put(hall.getId(), new WeakReference<>(hall));
        if(!cinema.getHalls().contains(hall))
            cinema.getHalls().add(hall);
    }

    @Override
    public void update(@NotNull Hall hall, @NotNull Cinema cinema, @NotNull Consumer<Hall> apply) throws DatabaseFailedException, InvalidIdException {
        if(hall.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");
        if(!cinema.getHalls().contains(hall))
            throw new DatabaseFailedException("This hall doesn't belong to this cinema");
        Hall copy = new Hall(hall);
        apply.accept(copy);
        hallDao.update(hall, copy, cinema);
        hall.copy(copy);
    }

    @Override
    public void delete(@NotNull Hall hall, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException {
        if (hall.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        if (!cinema.getHalls().contains(hall))
            throw new DatabaseFailedException("This hall doesn't belong to this cinema");
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
        cinema.getHalls().remove(hall);
    }

    @Override
    public Hall get(@NotNull ShowTime showTime, @NotNull Cinema cinema) throws InvalidIdException, DatabaseFailedException {
        if(showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database.");
        if(!cinema.getShowTimes().contains(showTime))
            throw new DatabaseFailedException("This showtime doesn't belong to this cinema");
        Hall hall = hallDao.get(showTime, cinema);
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
    public void update(@NotNull DatabaseEntity entity) throws DatabaseFailedException, InvalidIdException {
        if (entity instanceof Cinema) {
            for(Hall h : ((Cinema) entity).getHalls()) {
                notifyObservers(h);
                entities.remove(h.getId());
            }
        }
    }

    @Override
    public HashMap<Integer, WeakReference<Hall>> getEntities() {
        return entities;
    }



}
