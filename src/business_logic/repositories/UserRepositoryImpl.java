package business_logic.repositories;

import business_logic.CinemaDatabase;
import business_logic.Subject;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.exceptions.NotEnoughFundsException;
import daos.UserDao;
import daos.UserDaoImpl;
import domain.Booking;
import domain.DatabaseEntity;
import domain.Seat;
import domain.User;
import org.jetbrains.annotations.NotNull;
import utils.ThrowingConsumer;

import javax.xml.crypto.Data;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
        try {
            CinemaDatabase.withTransaction(() -> {
                userDao.delete(user);
                notifyObservers(user);
            });
        } catch (Exception e){
            if(e instanceof DatabaseFailedException)
                throw (DatabaseFailedException) e;
            if(e instanceof InvalidIdException)
                throw (InvalidIdException) e;
            throw new RuntimeException(e);
        }
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
    public void update(@NotNull DatabaseEntity entity) throws DatabaseFailedException, InvalidIdException {
        notifyObservers(entity);
        if(!(entity instanceof User)) {
            for (Map.Entry<Integer, WeakReference<User>> entrySet : entities.entrySet()) {
                WeakReference<User> value = entrySet.getValue();
                Integer key = entrySet.getKey();
                User usr = value != null ? value.get() : null;
                if (usr == null) {
                    entities.remove(key);
                } else {
                    for (Iterator<Booking> it = usr.getBookings().iterator(); it.hasNext();) {
                        Booking b = it.next();
                        if (entity == b.getShowTime() || (entity instanceof Seat && b.getSeats().contains(entity))) {
                            try {
                                update(usr, (u) -> u.setBalance(u.getBalance() + b.getCost()));
                            } catch (NotEnoughFundsException e) {
                                throw new RuntimeException(e);
                            }
                            it.remove();
                        }
                    }
                }
            }
        }
    }

    @Override
    public HashMap<Integer, WeakReference<User>> getEntities() {
        return entities;
    }
}
