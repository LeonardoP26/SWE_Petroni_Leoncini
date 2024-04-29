package business_logic.services;

import business_logic.CinemaDatabase;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.exceptions.InvalidSeatException;
import business_logic.exceptions.NotEnoughFundsException;
import daos.*;
import domain.*;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

public class DatabaseServiceImpl implements DatabaseService {

    private final CinemaDao cinemaDao;
    private final HallDao hallDao;
    private final MovieDao movieDao;
    private final ShowTimeDao showTimeDao;
    private final SeatsDao seatsDao;
    private final UserDao userDao;
    private final BookingDao bookingDao;
    private static DatabaseServiceImpl instance = null;

    public static DatabaseServiceImpl getInstance(CinemaDao cinemaRepo, HallDao hallRepo, MovieDao movieRepo, ShowTimeDao showTimeRepo, SeatsDao seatsRepo, UserDao userRepo, BookingDao bookingRepo){
        if(instance == null)
            instance = new DatabaseServiceImpl(cinemaRepo, hallRepo, movieRepo, showTimeRepo, seatsRepo, userRepo, bookingRepo);
        return instance;
    }

    public static DatabaseServiceImpl getInstance(){
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
    }

    private DatabaseServiceImpl() {
        this.cinemaDao = CinemaDaoImpl.getInstance();
        this.hallDao = HallDaoImpl.getInstance();
        this.movieDao = MovieDaoImpl.getInstance();
        this.showTimeDao = ShowTimeDaoImpl.getInstance();
        this.seatsDao = SeatsDaoImpl.getInstance();
        this.userDao = UserDaoImpl.getInstance();
        this.bookingDao = BookingDaoImpl.getInstance();
    }

    @Override
    public void addHall(@NotNull Hall hall, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException {
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema must be in the database before adding halls to it.");
        hallDao.insert(hall, cinema.getId());
        cinema.setHalls(cinema.getHalls() == null ? List.of(hall) : Stream.concat(cinema.getHalls().stream(), Stream.of(hall)).toList());

    }

    @Override
    public void addSeat(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException {
        if(hall.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall must be in the database before adding seats to it.");
        seatsDao.insert(seat, hall.getId());
        hall.setSeats(hall.getSeats() == null ? List.of(seat) : Stream.concat(hall.getSeats().stream(), Stream.of(seat)).toList());
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
        if(showTime.getMovie().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("The movie is not in the database.");
        if(showTime.getHall().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("The hall is not in the database.");
        if(showTime.getCinema().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("The cinema is not in the database.");
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
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("You need to add this cinema in the database before planning a movie show.");
        if (hall.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("You need to add this hall in the database before planning a movie show.");
        if(cinema.getHalls().stream().noneMatch(h -> h.getId() == hall.getId()))
            throw new DatabaseFailedException("The hall does not belong to this cinema.");
        ShowTime sht = new ShowTime(movie, hall, date);
        addShowTime(sht);
        cinema.setMovies(Stream.concat(cinema.getMovies().stream() ,Stream.of(movie)).toList());
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
        if(booking.getSeats().stream().anyMatch(s -> s.getId() == DatabaseEntity.ENTITY_WITHOUT_ID))
            throw new InvalidIdException("These seats are not in the database");
        if(booking.getShowTime().getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This showtime is not in the database");
        if(user.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This user is not in the database");
        bookingDao.insert(booking.getShowTime(), booking.getSeats(), user);
    }

    @Override
    public List<Cinema> retrieveCinemas() {
        try {
            return cinemaDao.get();
        } catch(Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public List<Movie> retrieveCinemaMovies(@NotNull Cinema cinema) {
        return movieDao.get(cinema);
    }

    @Override
    public List<ShowTime> retrieveMovieShowTimes(@NotNull Movie movie) {
        return showTimeDao.get(movie);
    }

    @Override
    public List<Seat> retrieveShowTimeHallSeats(@NotNull ShowTime showTime) {
        return seatsDao.get(showTime);
    }

    @Override
    public User login(String username, String password) {
        return userDao.get(username, encryptPassword(password));
    }

    @Override
    public User register(String username, String password) throws DatabaseFailedException {
        User newUser = new User(username, encryptPassword(password));
        addUser(newUser);
        return newUser;
    }

    @Override
    public User retrieveUser(String username) {
        return userDao.get(username);
    }

    @Override
    public void rechargeAccount(@NotNull User user, long amount) throws NotEnoughFundsException, DatabaseFailedException {
        user.setBalance(user.getBalance() + amount);
    }

    @Override
    public boolean pay(@NotNull Booking booking, @NotNull User owner, long cost) throws NotEnoughFundsException, InvalidSeatException, DatabaseFailedException {
        if(booking.getSeats().stream().anyMatch(Seat::isBooked))
            throw new InvalidSeatException("Some of these seats are already taken.");
        try{
            CinemaDatabase.withTransaction(() -> {
                addBooking(booking, owner);
                owner.setBalance(owner.getBalance() - cost);
            });
            owner.setBookings(Stream.concat(owner.getBookings().stream(), Stream.of(booking)).toList());
        } catch (Exception e){
            if(e instanceof NotEnoughFundsException)
                throw (NotEnoughFundsException) e;
            else if(e instanceof DatabaseFailedException)
                throw (DatabaseFailedException) e;
            else throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public void deleteUser(User user) throws DatabaseFailedException {
        userDao.delete(user);
    }

    @Override
    public List<Booking> retrieveBookings(User user) {
        return bookingDao.get(user);
    }

    @Override
    public void deleteBooking(@NotNull Booking booking) throws DatabaseFailedException {
        bookingDao.delete(booking);
    }

    @Override
    public Hall retrieveShowTimeHall(@NotNull ShowTime showTime) {
        return hallDao.get(showTime);
    }



}
