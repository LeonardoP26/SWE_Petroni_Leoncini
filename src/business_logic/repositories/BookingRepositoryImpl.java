package business_logic.repositories;

import business_logic.CinemaDatabase;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.exceptions.NotEnoughFundsException;
import daos.BookingDao;
import daos.BookingDaoImpl;
import domain.*;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class BookingRepositoryImpl implements BookingRepository {

    private static WeakReference<BookingRepository> instance = null;
    private final HashMap<Integer, WeakReference<Booking>> entities = new HashMap<>();
    private final BookingDao bookingDao;

    private BookingRepositoryImpl(BookingDao bookingDao){
        this.bookingDao = bookingDao;
    }

    public static @NotNull BookingRepository getInstance() {
        return getInstance(BookingDaoImpl.getInstance(CinemaDatabase.DB_URL));
    }

    public static @NotNull BookingRepository getInstance(BookingDao bookingDao) {
        BookingRepository inst = instance != null ? instance.get() : null;
        if(inst != null)
            return inst;
        inst = new BookingRepositoryImpl(bookingDao);
        instance = new WeakReference<>(inst);
        return inst;
    }

    @Override
    public void insert(@NotNull Booking booking, @NotNull User user) throws DatabaseFailedException, InvalidIdException, NotEnoughFundsException {
        List<Seat> seats = booking.getSeats();
        ShowTime showTime = booking.getShowTime();
        if(seats == null)
            throw new DatabaseFailedException("Seats list is null.");
        if(showTime == null)
            throw new DatabaseFailedException("Showtime list is null.");
        if (seats.stream().anyMatch(s -> s.getId() == DatabaseEntity.ENTITY_WITHOUT_ID))
            throw new InvalidIdException("These seats are not in the database");
        if (showTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database");
        if (user.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This user is not in the database");
        User copy = new User(user);
        copy.setBalance(copy.getBalance() - (long) booking.getSeats().size() * booking.getShowTime().getHall().getCost());
        bookingDao.insert(booking, user, copy);
        entities.put(booking.getId(), new WeakReference<>(booking));
        user.setBalance(copy.getBalance());
        user.getBookings().add(booking);
    }

    @Override
    public void update(@NotNull Booking oldBooking, @NotNull Booking newBooking, @NotNull User user) throws NotEnoughFundsException, DatabaseFailedException, InvalidIdException {
        List<Seat> oldSeats = oldBooking.getSeats();
        List<Seat> newSeats = newBooking.getSeats();
        ShowTime oldShowTime = oldBooking.getShowTime();
        ShowTime newShowTime = newBooking.getShowTime();
        if(oldSeats == null || newSeats == null)
            throw new DatabaseFailedException("Seats list is null.");
        if(oldShowTime == null || newShowTime == null)
            throw new DatabaseFailedException("Showtime list is null.");
        if (Stream.concat(oldSeats.stream(), newSeats.stream()).anyMatch(s -> s.getId() == DatabaseEntity.ENTITY_WITHOUT_ID))
            throw new InvalidIdException("These seats are not in the database");
        if (oldShowTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID || newShowTime.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database");
        if (user.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This user is not in the database");
        User copy = new User(user);
        long newCost = ((long) newBooking.getShowTime().getHall().getCost() * newBooking.getSeats().size() -
                (long) oldBooking.getShowTime().getHall().getCost() * oldBooking.getSeats().size());
        copy.setBalance(copy.getBalance() - newCost);
        bookingDao.update(oldBooking, newBooking, user, copy);
        user.getBookings().remove(oldBooking);
        user.getBookings().add(newBooking);
        user.setBalance(copy.getBalance());
        oldBooking.resetId();
    }


    @Override
    public void delete(@NotNull Booking booking, @NotNull User user) throws DatabaseFailedException, InvalidIdException {
        if (booking.getBookingNumber() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This booking is already not in the database");
        if(user.getBookings() == null || user.getBookings().isEmpty() || !user.getBookings().contains(booking))
            throw new DatabaseFailedException("This booking does not belong to this user.");
        if(booking.getShowTime() == null)
            throw new DatabaseFailedException("Show time is null");
        if(booking.getShowTime().getHall() == null)
            throw new DatabaseFailedException("Show time's hall is null");
        if(booking.getSeats() == null)
            throw new DatabaseFailedException("Seats are null");
        bookingDao.delete(booking, user);
        user.getBookings().remove(booking);
        booking.resetId();
    }

    @Override
    public List<Booking> get(@NotNull User user) throws InvalidIdException {
        if(user.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This user is not in the database.");
        List<Booking> bookings = bookingDao.get(user);
        return bookings.stream().map(b -> {
            Booking cached = entities.get(b.getId()) != null ? entities.get(b.getId()).get() : null;
            if(cached == null){
                entities.put(b.getId(), new WeakReference<>(b));
                cached = b;
            } else {
                cached.setShowTime(b.getShowTime());
                cached.setSeats(b.getSeats());
            }
            if(!user.getBookings().contains(cached))
                user.getBookings().add(cached);
            return cached;
        }).toList();
    }


    @Override
    public void update(@NotNull DatabaseEntity entity) {
        if(entity instanceof User || entity instanceof ShowTime || entity instanceof Seat){
            entities.forEach((key, value) -> {
                Booking b = value != null ? value.get() : null;
                if (b == null) {
                    entities.remove(key);
                } else if (entity instanceof User && ((User) entity).getBookings().contains(b)){
                    entities.remove(b.getId());
                    b.resetId();
                } else if (entity == b.getShowTime() || (entity instanceof Seat && b.getSeats().contains(entity))){
                    entities.remove(b.getId());
                    b.resetId();
                }
            });
        }
    }

    @Override
    public HashMap<Integer, WeakReference<Booking>> getEntities() {
        return entities;
    }
}
