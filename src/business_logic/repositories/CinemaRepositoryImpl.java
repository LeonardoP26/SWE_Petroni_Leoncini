package business_logic.repositories;

import business_logic.CinemaDatabase;
import business_logic.Subject;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import daos.CinemaDao;
import daos.CinemaDaoImpl;
import domain.Cinema;
import domain.DatabaseEntity;
import domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class CinemaRepositoryImpl extends Subject<DatabaseEntity> implements CinemaRepository {

    private static WeakReference<CinemaRepository> instance = null;
    private final WeakHashMap<Integer, WeakReference<Cinema>> entities = new WeakHashMap<>();
    private final CinemaDao cinemaDao;

    private CinemaRepositoryImpl(CinemaDao cinemaDao, HallRepository hallRepo){
        this.cinemaDao = cinemaDao;
        addObserver(hallRepo);
    }

    public static @NotNull CinemaRepository getInstance(){
        return getInstance(
                CinemaDaoImpl.getInstance(CinemaDatabase.DB_URL),
                HallRepositoryImpl.getInstance()
        );
    }

    public static @NotNull CinemaRepository getInstance(@NotNull CinemaDao cinemaDao, @NotNull HallRepository hallRepo) {
        CinemaRepository inst = instance != null ? instance.get() : null;
        if(inst != null)
            return inst;
        inst = new CinemaRepositoryImpl(cinemaDao, hallRepo);
        instance = new WeakReference<>(inst);
        return inst;
    }

    @Override
    public void insert(@NotNull Cinema cinema) throws DatabaseFailedException {
        cinemaDao.insert(cinema);
        entities.put(cinema.getId(), new WeakReference<>(cinema));
    }

    @Override
    public void update(@NotNull Cinema cinema, @NotNull Consumer<Cinema> edits) throws DatabaseFailedException, InvalidIdException {
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");
        Cinema copy = new Cinema(cinema);
        edits.accept(cinema);
        cinemaDao.update(cinema, copy);
        cinema.copy(copy);
    }

    @Override
    public void delete(@NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException {
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");
        cinemaDao.delete(cinema);
        notifyObservers(cinema);
        entities.remove(cinema.getId());
        cinema.resetId();
    }

    @Override
    public List<Cinema> get(){
        List<Cinema> cinemas = cinemaDao.get();
        return cinemas.stream().map(c ->{
            Cinema cached = entities.get(c.getId()) != null ? entities.get(c.getId()).get() : null;
            if(cached == null) {
                entities.put(c.getId(), new WeakReference<>(c));
                return c;
            }
            cached.setName(c.getName());
            return cached;
        }).toList();
    }

    @Override
    public void update(@NotNull DatabaseEntity entity) {
        if(entity instanceof Movie){
            entities.forEach((key, value) -> {
                Cinema cinema = value != null ? value.get() : null;
                if (cinema != null)
                    cinema.getMovies().remove(entity);
            });
        }
    }
}
