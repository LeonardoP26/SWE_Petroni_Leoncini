package business_logic.repositories;

import business_logic.CinemaDatabase;
import business_logic.Subject;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.exceptions.NotEnoughFundsException;
import daos.UserDao;
import daos.UserDaoImpl;
import domain.DatabaseEntity;
import domain.Seat;
import domain.User;
import org.jetbrains.annotations.NotNull;
import utils.ThrowingConsumer;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class UserRepositoryImpl extends Subject<DatabaseEntity> implements UserRepository {

    private static WeakReference<UserRepository> instance = null;
    private final HashMap<Integer, WeakReference<User>> entities = new HashMap<>();
    private final UserDao userDao;

    private UserRepositoryImpl(UserDao userDao, BookingRepository bookingRepo){
        this.userDao = userDao;
        addObserver(bookingRepo);
    }

    public static @NotNull UserRepository getInstance(){
        return getInstance(
                UserDaoImpl.getInstance(CinemaDatabase.DB_URL),
                BookingRepositoryImpl.getInstance()
        );
    }

    public static @NotNull UserRepository getInstance(@NotNull UserDao userDao, @NotNull BookingRepository bookingRepo) {
        UserRepository inst = instance != null ? instance.get() : null;
        if(inst != null)
            return inst;
        inst = new UserRepositoryImpl(userDao, bookingRepo);
        instance = new WeakReference<>(inst);
        return inst;
    }

    @Override
    public void insert(@NotNull User user) throws DatabaseFailedException {
        userDao.insert(user);
        entities.put(user.getId(), new WeakReference<>(user));
    }

    @Override
    public void update(@NotNull User user, ThrowingConsumer<User> edits) throws NotEnoughFundsException, DatabaseFailedException, InvalidIdException {
        if(user.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This user is not in the database.");
        User copy = new User(user);
        try {
            edits.accept(copy);
        } catch (Exception e){
            if(e instanceof NotEnoughFundsException)
                throw (NotEnoughFundsException) e;
            else throw new RuntimeException(e);
        }
        userDao.update(user, copy);
        user.copy(copy);
    }

    @Override
    public void delete(@NotNull User user) throws DatabaseFailedException, InvalidIdException {
        if(user.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This user is not in the database.");
        userDao.delete(user);
        notifyObservers(user);
        entities.remove(user.getId());
        user.resetId();
    }

    @Override
    public User get(String username, String password){
        User user = userDao.get(username, password);
        if(user == null)
            return null;
        User cached = entities.get(user.getId()) != null ? entities.get(user.getId()).get() : null;
        if(cached == null){
            entities.put(user.getId(), new WeakReference<>(user));
            return user;
        }
        cached.copy(user);
        return cached;
    }


    @Override
    public void update(@NotNull DatabaseEntity entity) {
        notifyObservers(entity);
        if(!(entity instanceof User))
            entities.forEach((key, value) -> {
                User usr = value != null ? value.get() : null;
                if(usr == null){
                    entities.remove(key);
                } else {
                    usr.getBookings().removeIf(b ->
                        entity == b.getShowTime() || (entity instanceof Seat && b.getSeats().contains(entity))
                    );
                }
            });
    }
}
