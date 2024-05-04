package business_logic.services;

import business_logic.CinemaDatabase;
import business_logic.ReceiptPrinter;
import business_logic.Subject;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.exceptions.InvalidSeatException;
import business_logic.exceptions.NotEnoughFundsException;
import daos.*;
import domain.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DatabaseServiceImpl extends Subject<Booking> implements DatabaseService {

    private final ReceiptPrinter receiptPrinter = ReceiptPrinter.getInstance();
    private final CinemaDao cinemaDao;
    private final HallDao hallDao;
    private final MovieDao movieDao;
    private final ShowTimeDao showTimeDao;
    private final SeatsDao seatsDao;
    private final UserDao userDao;
    private final BookingDao bookingDao;
    private static DatabaseService instance = null;

    public static DatabaseService getInstance(CinemaDao cinemaRepo, HallDao hallRepo, MovieDao movieRepo, ShowTimeDao showTimeRepo, SeatsDao seatsRepo, UserDao userRepo, BookingDao bookingRepo){
        if(instance == null)
            instance = new DatabaseServiceImpl(cinemaRepo, hallRepo, movieRepo, showTimeRepo, seatsRepo, userRepo, bookingRepo);
        return instance;
    }

    public static DatabaseService getInstance(){
        if(instance == null)
            instance = new DatabaseServiceImpl();
        return instance;
    }

    private String encryptPassword(@NotNull String password) {
        try{
            byte[] rawPassword = MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawPassword);
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }


    private DatabaseServiceImpl(CinemaDao cinemaDao, HallDao hallDao, MovieDao movieDao, ShowTimeDao showTimeDao, SeatsDao seatsDao, UserDao userDao, BookingDao bookingDao){
        this.cinemaDao = cinemaDao;
        this.hallDao = hallDao;
        this.movieDao = movieDao;
        this.showTimeDao = showTimeDao;
        this.seatsDao = seatsDao;
        this.userDao = userDao;
        this.bookingDao = bookingDao;
        addObserver(receiptPrinter);
    }

    private DatabaseServiceImpl() {
        this(CinemaDaoImpl.getInstance(),
                HallDaoImpl.getInstance(),
                MovieDaoImpl.getInstance(),
                ShowTimeDaoImpl.getInstance(),
                SeatsDaoImpl.getInstance(),
                UserDaoImpl.getInstance(),
                BookingDaoImpl.getInstance()
        );
    }

    @Override
    public void addHall(@NotNull Hall hall, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException {
        hallDao.insert(hall, cinema);
        if(cinema.getHalls() == null)
            cinema.setHalls(new ArrayList<>());
        cinema.getHalls().add(hall);
    }

    @Override
    public void addSeat(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException {
        seatsDao.insert(seat, hall);
        if(hall.getSeats() == null)
            hall.setSeats(new ArrayList<>());
        hall.getSeats().add(seat);
    }

    /**
     *
     * Method to add a movie into the database.
     *
     * @param movie the movie to add into the database;
     */

    @Override
    public void addMovie(@NotNull Movie movie) throws DatabaseFailedException {
        movieDao.insert(movie);
    }

    @Override
    public void addShowTime(@NotNull ShowTime showTime) throws DatabaseFailedException, InvalidIdException {
        showTimeDao.insert(showTime);
    }

    /**
     *
     * Method to add a movie already in the database to a cinema creating a new {@link ShowTime#ShowTime(Movie, Hall, LocalDateTime) ShowTime}
     *
     * @param movie the movie to show
     * @param cinema in which cinema the movie's show will take place
     * @param hall in which hall the movie's show will take place, must be a hall of the cinema
     * @param date when the movie's show will start
     * @throws DatabaseFailedException if the hall does not belong to this cinema.
     * @throws InvalidIdException if cinema id and hall id are equal to {@link DatabaseEntity#ENTITY_WITHOUT_ID ENTITY_WITHOUT_ID}
     */
    @Override
    public void addMovie(@NotNull Movie movie, @NotNull Cinema cinema, @NotNull Hall hall, LocalDateTime date) throws DatabaseFailedException, InvalidIdException {
        ShowTime sht = new ShowTime(movie, hall, date);
        addShowTime(sht);
        cinema.getMovies().add(movie);
    }

    @Override
    public void addCinema(@NotNull Cinema cinema) throws DatabaseFailedException {
        cinemaDao.insert(cinema);
    }

    @Override
    public void addUser(@NotNull User user) throws DatabaseFailedException {
        userDao.insert(user);
    }

    @Override
    public void addBooking(@NotNull Booking booking, User user) throws DatabaseFailedException, InvalidIdException {
        bookingDao.insert(booking, user);
        user.getBookings().add(booking);
    }

    @Override
    public List<Cinema> retrieveCinemas() {
        return cinemaDao.get();
    }

    @Override
    public List<Movie> retrieveCinemaMovies(@NotNull Cinema cinema) throws InvalidIdException {
        return movieDao.get(cinema);
    }

    @Override
    public List<ShowTime> retrieveMovieShowTimes(@NotNull Movie movie) throws InvalidIdException {
        return showTimeDao.get(movie);
    }

    @Override
    public List<Seat> retrieveShowTimeHallSeats(@NotNull ShowTime showTime) throws InvalidIdException {
        return seatsDao.get(showTime);
    }

    @Override
    public User login(String username, String password) {
        return userDao.get(username, encryptPassword(password));
    }

    @Override
    public User register(String username, String password) throws DatabaseFailedException {
        User newUser = new User(username, encryptPassword(password));
        if(newUser.getUsername().isBlank())
            newUser.setUsername(null);
        if(newUser.getPassword().isBlank())
            newUser.setPassword(null);
        addUser(newUser);
        return newUser;
    }

    @Override
    public void rechargeAccount(@NotNull User user, long amount) throws NotEnoughFundsException, DatabaseFailedException {
        user.setBalance(user.getBalance() + amount);
    }

    @Override
    public void pay(@NotNull Booking booking, @Nullable Booking oldBooking, @NotNull User user, long cost) throws NotEnoughFundsException, InvalidSeatException, DatabaseFailedException {
        if(booking.getSeats().stream().anyMatch(Seat::isBooked))
            throw new InvalidSeatException("Some of these seats are already taken.");
        user.setBalance(user.getBalance() - cost);
        try{
            CinemaDatabase.withTransaction(() -> {
                if(oldBooking != null)
                    deleteBooking(oldBooking);
                addBooking(booking, user);
                userDao.update(user);
            });
            booking.getSeats().forEach(s -> s.setBooked(true));
            notifyObservers(booking);
        } catch (Exception e){
            user.getBookings().remove(booking);
            if(e instanceof NotEnoughFundsException)
                throw (NotEnoughFundsException) e;
            else if(e instanceof DatabaseFailedException) {
                user.setBalance(user.getBalance() + cost);
                throw (DatabaseFailedException) e;
            }
            else throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUser(User user) throws DatabaseFailedException, InvalidIdException {
        userDao.delete(user);
    }

    @Override
    public List<Booking> retrieveBookings(User user) throws InvalidIdException {
        return bookingDao.get(user);
    }

    @Override
    public void deleteBooking(@NotNull Booking booking) throws DatabaseFailedException, InvalidIdException {
        bookingDao.delete(booking);
    }

    @Override
    public Hall retrieveShowTimeHall(@NotNull ShowTime showTime) throws InvalidIdException {
        return hallDao.get(showTime);
    }



}
