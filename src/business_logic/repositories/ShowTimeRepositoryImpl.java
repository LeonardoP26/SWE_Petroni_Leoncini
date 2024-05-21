package business_logic.repositories;

import business_logic.CinemaDatabase;
import business_logic.Subject;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import daos.ShowTimeDao;
import daos.ShowTimeDaoImpl;
import domain.Cinema;
import domain.DatabaseEntity;
import domain.Movie;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        if(showTime.getHall().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        if(showTime.getMovie().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This movie is not in the database.");

        showTimeDao.insert(showTime);
        entities.put(showTime.getId(), new WeakReference<>(showTime));
    }

    @Override
    public void update(@NotNull ShowTime showTime, @NotNull Consumer<ShowTime> edits) throws DatabaseFailedException, InvalidIdException {
        if(showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database.");

        ShowTime copy = new ShowTime(showTime);
        edits.accept(copy);
        if(showTime.getHall() == null)
            throw new DatabaseFailedException("Hall cannot be null");
        if(showTime.getMovie() == null)
            throw new DatabaseFailedException("Movie cannot be null");
        if(copy.getHall().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        if(copy.getMovie().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This movie is not in the database.");
        showTimeDao.update(showTime, copy);
        showTime.copy(copy);
    }

    @Override
    public void delete(@NotNull ShowTime showTime) throws DatabaseFailedException, InvalidIdException {
        if (showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database.");
        try {
            CinemaDatabase.withTransaction(() -> {
                showTimeDao.delete(showTime);
                notifyObservers(showTime);
            });
        } catch (Exception e) {
            if (e instanceof DatabaseFailedException)
                throw (DatabaseFailedException) e;
            if (e instanceof InvalidIdException)
                throw (InvalidIdException) e;
            throw new RuntimeException(e);
        }
        entities.remove(showTime.getId());
        showTime.resetId();
    }

    @Override
    public List<ShowTime> get(@NotNull Movie movie, @NotNull Cinema cinema) throws InvalidIdException, DatabaseFailedException {
        if(movie.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This movie is not in the database.");

        List<ShowTime> showTimes = showTimeDao.get(movie, cinema);
        return showTimes.stream().map(this::findForCaching).toList();
    }

    @Override
    public HashMap<Integer, WeakReference<ShowTime>> getEntities() {
        return entities;
    }

    @Override
    public void update(@NotNull DatabaseEntity entity) throws DatabaseFailedException, InvalidIdException {
        for(Map.Entry<Integer, WeakReference<ShowTime>> entrySet: entities.entrySet()){
            Integer key = entrySet.getKey();
            ShowTime st = entrySet.getValue() != null ? entrySet.getValue().get() : null;
            if (st == null) {
                entities.remove(key);
            } else if (st.getHall() == entity || st.getMovie() == entity) {
                notifyObservers(st);
                entities.remove(key);
                st.resetId();
            }
        }
    }

    @Override
    public ShowTime get(ShowTime showTime) throws InvalidIdException {
        if(showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database.");
        return findForCaching(showTimeDao.get(showTime));
    }


    private ShowTime findForCaching(ShowTime showTime){
        ShowTime cached = entities.get(showTime.getId()) != null ? entities.get(showTime.getId()).get() : null;
        if(cached == null) {
            entities.put(showTime.getId(), new WeakReference<>(showTime));
            return showTime;
        }
        cached.copy(showTime);
        return cached;
    }

}
