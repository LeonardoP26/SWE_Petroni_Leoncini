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

import javax.xml.crypto.Data;
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
    public void insert(@NotNull ShowTime showTime, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException {
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");
        if(showTime.getHall().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        if(showTime.getMovie().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This movie is not in the database.");
        if(!cinema.getHalls().contains(showTime.getHall()))
            throw new DatabaseFailedException("This showtime does not belong to this cinema");

        showTimeDao.insert(showTime);
        entities.put(showTime.getId(), new WeakReference<>(showTime));
        cinema.getShowTimes().add(showTime);
        if(!cinema.getMovies().contains(showTime.getMovie()))
            cinema.getMovies().add(showTime.getMovie());
    }

    @Override
    public void update(@NotNull ShowTime showTime, @NotNull Cinema cinema, @NotNull Consumer<ShowTime> edits) throws DatabaseFailedException, InvalidIdException {
        if(showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database.");
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");
        if(!cinema.getShowTimes().contains(showTime))
            throw new DatabaseFailedException("This showtime does not belong to this cinema");
        if(!cinema.getHalls().contains(showTime.getHall()))
            throw new DatabaseFailedException("This showtime hall does not belong to this cinema");

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
        if(!cinema.getHalls().contains(copy.getHall()))
            throw new DatabaseFailedException("This showtime hall does not belong to this cinema");
        showTimeDao.update(showTime, copy);
        showTime.copy(copy);
    }

    @Override
    public void delete(@NotNull ShowTime showTime, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException {
        if (showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database.");
        if (!cinema.getHalls().contains(showTime.getHall()))
            throw new DatabaseFailedException("This showtime hall does not belong to this cinema");
        if (!cinema.getShowTimes().contains(showTime))
            throw new DatabaseFailedException("This showtime does not belong to this cinema");
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
        cinema.getShowTimes().remove(showTime);
        entities.remove(showTime.getId());
        if (cinema.getShowTimes().stream().noneMatch(sht -> sht.getMovie() == showTime.getMovie()))
            cinema.getMovies().remove(showTime.getMovie());
        showTime.resetId();
    }

    @Override
    public List<ShowTime> get(@NotNull Movie movie, @NotNull Cinema cinema) throws InvalidIdException, DatabaseFailedException {
        if(movie.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This movie is not in the database.");
        if(!cinema.getMovies().contains(movie))
            throw new DatabaseFailedException("This movie does not belong to this cinema");

        List<ShowTime> showTimes = showTimeDao.get(movie, cinema);
        return showTimes.stream().map(st -> {
            ShowTime cached = entities.get(st.getId()) != null ? entities.get(st.getId()).get() : null;
            if(cached == null) {
                entities.put(st.getId(), new WeakReference<>(st));
                return st;
            }
            cached.copy(st);
            if(!cinema.getShowTimes().contains(cached))
                cinema.getShowTimes().add(cached);
            return cached;
        }).toList();
    }

    @Override
    public HashMap<Integer, WeakReference<ShowTime>> getEntities() {
        return entities;
    }

    @Override
    public void update(@NotNull DatabaseEntity entity) throws DatabaseFailedException, InvalidIdException {
        for(Map.Entry<Integer, WeakReference<ShowTime>> entrySet: entities.entrySet()){
            Integer key = entrySet.getKey();
            WeakReference<ShowTime> value = entrySet.getValue();
            ShowTime st = value != null ? value.get() : null;
            if (st == null) {
                entities.remove(key);
            } else if (st.getHall() == entity || st.getMovie() == entity) {
                notifyObservers(st);
                entities.remove(key);
            }
        }
    }

}
