package business_logic.repositories;

import business_logic.CinemaDatabase;
import business_logic.Subject;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import daos.SeatDao;
import daos.SeatDaoImpl;
import domain.DatabaseEntity;
import domain.Hall;
import domain.Seat;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class SeatRepositoryImpl extends Subject<DatabaseEntity> implements SeatRepository {

    private static WeakReference<SeatRepository> instance = null;
    private final HashMap<Integer, WeakReference<Seat>> entities = new HashMap<>();
    private final SeatDao seatDao;

    private SeatRepositoryImpl(SeatDao seatDao, UserRepository userRepo) {
        this.seatDao = seatDao;
        addObserver(userRepo);
    }

    public static @NotNull SeatRepository getInstance() {
        return getInstance(
                SeatDaoImpl.getInstance(CinemaDatabase.DB_URL),
                UserRepositoryImpl.getInstance()
        );
    }

    public static @NotNull SeatRepository getInstance(SeatDao seatDao, UserRepository userRepo) {
        SeatRepository inst = instance != null ? instance.get() : null;
        if(inst != null)
            return inst;
        inst = new SeatRepositoryImpl(seatDao, userRepo);
        instance = new WeakReference<>(inst);
        return inst;
    }

    @Override
    public void insert(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException {
        if(hall.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        seatDao.insert(seat, hall);
        entities.put(seat.getId(), new WeakReference<>(seat));
        hall.getSeats().add(seat);
    }

    @Override
    public void update(@NotNull Seat seat, @NotNull Hall hall, Consumer<Seat> edits) throws InvalidIdException, DatabaseFailedException {
        if(hall.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        if(seat.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This seat is not in the database.");
        if(!hall.getSeats().contains(seat))
            throw new DatabaseFailedException("This seat does not belong to this Hall.");
        Seat copy = new Seat(seat);
        edits.accept(copy);
        seat.copy(copy);
    }

    @Override
    public void update(@NotNull DatabaseEntity entity) throws DatabaseFailedException, InvalidIdException {
        if(entity instanceof Hall) {
            for(Seat s : ((Hall) entity).getSeats()) {
                notifyObservers(s);
                entities.remove(s.getId());
                s.resetId();
            }
        }
    }

    @Override
    public void delete(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException {
        if (seat.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This seat is not in the database.");
        if (!hall.getSeats().contains(seat))
            throw new DatabaseFailedException("This seat does not belong to this Hall.");
        try {
            CinemaDatabase.withTransaction(() -> {
                seatDao.delete(seat);
                notifyObservers(seat);
            });
        } catch (Exception e) {
            if (e instanceof DatabaseFailedException)
                throw (DatabaseFailedException) e;
            if (e instanceof InvalidIdException)
                throw (InvalidIdException) e;
            throw new RuntimeException(e);
        }
        hall.getSeats().remove(seat);
    }

    @Override
    public List<Seat> get(@NotNull ShowTime showTime) throws InvalidIdException {
        if(showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database.");
        List<Seat> seats = seatDao.get(showTime);
        seats = seats.stream().map(this::findForCaching).toList();
        return seats;
    }

    @Override
    public Seat get(Seat seat) throws InvalidIdException {
        if(seat.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This seat is not in the database.");
        return findForCaching(seatDao.get(seat));
    }



    @Override
    public HashMap<Integer, WeakReference<Seat>> getEntities() {
        return entities;
    }

    private Seat findForCaching(Seat seat){
        if(seat == null)
            return null;
        Seat cached = entities.get(seat.getId()) != null ? entities.get(seat.getId()).get() : null;
        if(cached == null) {
            entities.put(seat.getId(), new WeakReference<>(seat));
            cached = seat;
        } else {
            cached.setRow(seat.getRow());
            cached.setNumber(seat.getNumber());
            cached.setBooked(seat.isBooked());
        }
        return cached;
    }

}
