package business_logic.repositories;

import business_logic.CinemaDatabase;
import business_logic.Subject;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import daos.ShowTimeDao;
import daos.ShowTimeDaoImpl;
import domain.DatabaseEntity;
import domain.Movie;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ShowTimeRepositoryImpl extends Subject<DatabaseEntity> implements ShowTimeRepository {

    private static WeakReference<ShowTimeRepository> instance = null;
    private final HashMap<Integer, WeakReference<ShowTime>> entities = new HashMap<>();
    private final ShowTimeDao showTimeDao;

    private ShowTimeRepositoryImpl(@NotNull ShowTimeDao showTimeDao, UserRepository userRepo){
        this.showTimeDao = showTimeDao;
        addObserver(userRepo);
    }

    public static @NotNull ShowTimeRepository getInstance(){
        return getInstance(
                ShowTimeDaoImpl.getInstance(CinemaDatabase.DB_URL),
                UserRepositoryImpl.getInstance()
        );
    }

    public static @NotNull ShowTimeRepository getInstance(@NotNull ShowTimeDao showTimeDao, @NotNull UserRepository userRepo) {
        ShowTimeRepository inst = instance != null ? instance.get() : null;
        if(inst != null)
            return inst;
        inst = new ShowTimeRepositoryImpl(showTimeDao, userRepo);
        instance = new WeakReference<>(inst);
        return inst;
    }

    @Override
    public void insert(@NotNull ShowTime showTime) throws DatabaseFailedException, InvalidIdException {
        if(showTime.getCinema().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");
        if(showTime.getHall().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        if(showTime.getMovie().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This movie is not in the database.");
        showTimeDao.insert(showTime);
        entities.put(showTime.getId(), new WeakReference<>(showTime));
        if(!showTime.getCinema().getMovies().contains(showTime.getMovie()))
            showTime.getCinema().getMovies().add(showTime.getMovie());
    }

    @Override
    public void update(@NotNull ShowTime showTime, @NotNull Consumer<ShowTime> edits) throws DatabaseFailedException, InvalidIdException {
        if(showTime.getCinema() == null)
            throw new DatabaseFailedException("Cinema cannot be null");
        if(showTime.getHall() == null)
            throw new DatabaseFailedException("Hall cannot be null");
        if(showTime.getMovie() == null)
            throw new DatabaseFailedException("Movie cannot be null");
        if(showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database.");
        if(showTime.getCinema().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");
        if(showTime.getHall().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        if(showTime.getMovie().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This movie is not in the database.");
        ShowTime copy = new ShowTime(showTime);
        edits.accept(copy);
        showTimeDao.update(showTime, copy);
        showTime.copy(copy);
    }

    @Override
    public void delete(@NotNull ShowTime showTime) throws DatabaseFailedException, InvalidIdException {
        if(showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database.");
        showTimeDao.delete(showTime);
        notifyObservers(showTime);
        entities.remove(showTime.getId());
        if(entities.values().stream().noneMatch((st) -> {
            ShowTime sht = st != null ? st.get() : null;
            if(sht == null)
                return false;
            return sht.getMovie() == showTime.getMovie();
        }))
            showTime.getCinema().getMovies().remove(showTime.getMovie());
        showTime.resetId();
    }

    @Override
    public List<ShowTime> get(@NotNull Movie movie) throws InvalidIdException {
        if(movie.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This movie is not in the database.");
        List<ShowTime> showTimes = showTimeDao.get(movie);
        return showTimes.stream().map(st -> {
            ShowTime cached = entities.get(st.getId()) != null ? entities.get(st.getId()).get() : null;
            if(cached == null) {
                entities.put(st.getId(), new WeakReference<>(st));
                return st;
            }
            cached.copy(st);
            return cached;
        }).toList();
    }

    @Override
    public HashMap<Integer, WeakReference<ShowTime>> getEntities() {
        return entities;
    }

    @Override
    public void update(@NotNull DatabaseEntity entity) {
        entities.forEach((key, value) -> {
            ShowTime st = value != null ? value.get() : null;
            if (st == null) {
                entities.remove(key);
            } else if (st.getHall() == entity || st.getMovie() == entity) {
                notifyObservers(st);
                entities.remove(key);
            }
        });
    }
}
