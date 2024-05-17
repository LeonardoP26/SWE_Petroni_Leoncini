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
import java.util.ArrayList;
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
        if(!hall.getSeats().contains(seat))
            throw new DatabaseFailedException("This seat does not belong to this Hall.");
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
    public void update(@NotNull DatabaseEntity entity) {
        if(entity instanceof Hall) {
            ((Hall) entity).getSeats().forEach(s -> {
                notifyObservers(s);
                entities.remove(s.getId());
                s.resetId();
            });
        }
    }

    @Override
    public void delete(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException {
        if(seat.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This seat is not in the database.");
        if(!hall.getSeats().contains(seat))
            throw new DatabaseFailedException("This seat does not belong to this Hall.");
        seatDao.delete(seat);
        notifyObservers(seat);
        hall.getSeats().remove(seat);
    }

    @Override
    public List<Seat> get(@NotNull ShowTime showTime) throws InvalidIdException {
        if(showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database.");
        List<Seat> seats = seatDao.get(showTime);
        seats = seats.stream().map(s -> {
            Seat cached = entities.get(s.getId()) != null ? entities.get(s.getId()).get() : null;
            if(cached == null) {
                entities.put(s.getId(), new WeakReference<>(s));
                cached = s;
            } else {
                cached.setRow(s.getRow());
                cached.setNumber(s.getNumber());
                cached.setBooked(s.isBooked());
            }
            return cached;
        }).toList();
        showTime.getHall().setSeats(new ArrayList<>(seats));
        return seats;
    }


    @Override
    public HashMap<Integer, WeakReference<Seat>> getEntities() {
        return entities;
    }
}
