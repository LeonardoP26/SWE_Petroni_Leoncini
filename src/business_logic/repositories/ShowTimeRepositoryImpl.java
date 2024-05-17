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
        if(showTime.getHall() == null)
            throw new DatabaseFailedException("Hall cannot be null");
        if(showTime.getMovie() == null)
            throw new DatabaseFailedException("Movie cannot be null");
        if(showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database.");
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");
        if(showTime.getHall().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall is not in the database.");
        if(showTime.getMovie().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This movie is not in the database.");
        if(!cinema.getShowTimes().contains(showTime))
            throw new DatabaseFailedException("This showtime does not belong to this cinema");
        if(!cinema.getHalls().contains(showTime.getHall()))
            throw new DatabaseFailedException("This showtime hall does not belong to this cinema");

        ShowTime copy = new ShowTime(showTime);
        edits.accept(copy);
        showTimeDao.update(showTime, copy);
        showTime.copy(copy);
    }

    @Override
    public void delete(@NotNull ShowTime showTime, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException {
        if(showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database.");
        if(!cinema.getHalls().contains(showTime.getHall()))
            throw new DatabaseFailedException("This showtime hall does not belong to this cinema");
        if(!cinema.getShowTimes().contains(showTime))
            throw new DatabaseFailedException("This showtime does not belong to this cinema");

        showTimeDao.delete(showTime);
        notifyObservers(showTime);
        cinema.getShowTimes().remove(showTime);
        entities.remove(showTime.getId());
        if(entities.values().stream().noneMatch((st) -> {
            ShowTime sht = st != null ? st.get() : null;
            if(sht == null)
                return false;
            return sht.getMovie() == showTime.getMovie();
        }))
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
