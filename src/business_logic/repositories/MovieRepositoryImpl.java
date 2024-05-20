package business_logic.repositories;

import business_logic.CinemaDatabase;
import business_logic.Subject;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import daos.MovieDao;
import daos.MovieDaoImpl;
import domain.Cinema;
import domain.DatabaseEntity;
import domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class MovieRepositoryImpl extends Subject<DatabaseEntity> implements MovieRepository {

    private static WeakReference<MovieRepository> instance = null;
    private final HashMap<Integer, WeakReference<Movie>> entities = new HashMap<>();
    private final MovieDao movieDao;

    private MovieRepositoryImpl(MovieDao movieDao, ShowTimeRepository showTimeRepo, CinemaRepository cinemaRepo){
        this.movieDao = movieDao;
        addObserver(showTimeRepo);
        addObserver(cinemaRepo);
    }

    public static @NotNull MovieRepository getInstance(){
        return getInstance(
                MovieDaoImpl.getInstance(CinemaDatabase.DB_URL),
                ShowTimeRepositoryImpl.getInstance(),
                CinemaRepositoryImpl.getInstance()
        );
    }

    public static @NotNull MovieRepository getInstance(@NotNull MovieDao movieDao, @NotNull ShowTimeRepository showTimeRepo, @NotNull CinemaRepository cinemaRepo){
        MovieRepository inst = instance != null ? instance.get() : null;
        if(inst != null)
            return inst;
        inst = new MovieRepositoryImpl(movieDao, showTimeRepo, cinemaRepo);
        instance = new WeakReference<>(inst);
        return inst;
    }

    @Override
    public void insert(@NotNull Movie movie) throws DatabaseFailedException {
        movieDao.insert(movie);
        entities.put(movie.getId(), new WeakReference<>(movie));
    }

    @Override
    public void update(@NotNull Movie movie, @NotNull Cinema cinema, @NotNull Consumer<Movie> edits) throws DatabaseFailedException, InvalidIdException {
        if(movie.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This movie is not in the database.");
        Movie copy = new Movie(movie);
        edits.accept(copy);
        movieDao.update(movie, copy);
        movie.copy(copy);
        cinema.getMovies().add(movie);
    }

    @Override
    public void delete(@NotNull Movie movie) throws DatabaseFailedException, InvalidIdException {
        if (movie.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This movie is not in the database.");
        try {
            CinemaDatabase.withTransaction(() -> {
                movieDao.delete(movie);
                notifyObservers(movie);
            });
        } catch (Exception e) {
            if (e instanceof DatabaseFailedException)
                throw (DatabaseFailedException) e;
            if (e instanceof InvalidIdException)
                throw (InvalidIdException) e;
            throw new RuntimeException(e);
        }
        entities.remove(movie.getId());
        movie.resetId();
    }

    @Override
    public List<Movie> get(@NotNull Cinema cinema) throws InvalidIdException {
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema is not in the database.");
        List<Movie> movies = movieDao.get(cinema);
        return movies.stream().map(m -> {
            Movie cached = entities.get(m.getId()) != null ? entities.get(m.getId()).get() : null;
            if(cached == null) {
                entities.put(m.getId(), new WeakReference<>(m));
                cached = m;
            } else {
                cached.setName(m.getName());
                cached.setDuration(m.getDuration());
            }
            if(!cinema.getMovies().contains(cached))
                cinema.getMovies().add(cached);
            return cached;
        }).toList();
    }

}
