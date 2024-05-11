package business_logic.services;

import business_logic.ReceiptPrinter;
import business_logic.Subject;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.exceptions.InvalidSeatException;
import business_logic.exceptions.NotEnoughFundsException;
import business_logic.repositories.*;
import domain.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

public class DatabaseServiceImpl extends Subject<Booking> implements DatabaseService {

    private static DatabaseService instance = null;
    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;
    private final SeatRepository seatRepo;
    private final ShowTimeRepository showTimeRepo;
    private final MovieRepository movieRepo;
    private final HallRepository hallRepo;
    private final CinemaRepository cinemaRepo;

    public static DatabaseService getInstance(
            CinemaRepository cinemaRepo,
            HallRepository hallRepo,
            MovieRepository movieRepo,
            ShowTimeRepository showTimeRepo,
            SeatRepository seatRepo,
            UserRepository userRepo,
            BookingRepository bookingRepo
    ){
        if(instance == null)
            instance = new DatabaseServiceImpl(cinemaRepo, hallRepo, movieRepo, showTimeRepo, seatRepo, userRepo, bookingRepo);
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


    private DatabaseServiceImpl(
            CinemaRepository cinemaRepo,
            HallRepository hallRepo,
            MovieRepository movieRepo,
            ShowTimeRepository showTimeRepo,
            SeatRepository seatRepo,
            UserRepository userRepo,
            BookingRepository bookingRepo
    ){
        this.cinemaRepo = cinemaRepo;
        this.hallRepo = hallRepo;
        this.movieRepo = movieRepo;
        this.showTimeRepo = showTimeRepo;
        this.seatRepo = seatRepo;
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
        addObserver(ReceiptPrinter.getInstance());
    }

    private DatabaseServiceImpl() {
        this(CinemaRepositoryImpl.getInstance(),
                HallRepositoryImpl.getInstance(),
                MovieRepositoryImpl.getInstance(),
                ShowTimeRepositoryImpl.getInstance(),
                SeatRepositoryImpl.getInstance(),
                UserRepositoryImpl.getInstance(),
                BookingRepositoryImpl.getInstance()
        );
    }

    @Override
    public void addHall(@NotNull Hall hall, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException {
        hallRepo.insert(hall, cinema);
    }

    @Override
    public void addSeat(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException {
        seatRepo.insert(seat, hall);
    }

    /**
     *
     * Method to add a movie into the database.
     *
     * @param movie the movie to add into the database;
     */

    @Override
    public void addMovie(@NotNull Movie movie) throws DatabaseFailedException {
        movieRepo.insert(movie);
    }

    @Override
    public void addShowTime(@NotNull ShowTime showTime) throws DatabaseFailedException, InvalidIdException {
        showTimeRepo.insert(showTime);
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
    }

    @Override
    public void addCinema(@NotNull Cinema cinema) throws DatabaseFailedException {
        cinemaRepo.insert(cinema);
    }

    @Override
    public void addUser(@NotNull User user) throws DatabaseFailedException {
        userRepo.insert(user);
    }

    @Override
    public void addBooking(@NotNull Booking booking, User user) throws DatabaseFailedException, InvalidIdException, NotEnoughFundsException {
        bookingRepo.insert(booking, user);
    }

    @Override
    public List<Cinema> retrieveCinemas() {
        return cinemaRepo.get();
    }

    @Override
    public List<Movie> retrieveCinemaMovies(@NotNull Cinema cinema) throws InvalidIdException {
        return movieRepo.get(cinema);
    }

    @Override
    public List<ShowTime> retrieveMovieShowTimes(@NotNull Movie movie) throws InvalidIdException {
        return showTimeRepo.get(movie);
    }

    @Override
    public List<Seat> retrieveShowTimeHallSeats(@NotNull ShowTime showTime) throws InvalidIdException {
        return seatRepo.get(showTime);
    }

    @Override
    public User login(String username, String password) {
        return userRepo.get(username, encryptPassword(password));
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
    public void rechargeAccount(@NotNull User user, long amount) throws NotEnoughFundsException, DatabaseFailedException, InvalidIdException {
        userRepo.update(user, (usr) -> usr.setBalance(usr.getBalance() + amount));
    }

    @Override
    public void pay(@NotNull Booking booking, @Nullable Booking oldBooking, @NotNull User user, long cost) throws NotEnoughFundsException, InvalidSeatException, DatabaseFailedException, InvalidIdException {
        if(booking.getSeats().stream().anyMatch(Seat::isBooked))
            throw new InvalidSeatException("Some of these seats are already taken.");
        if(oldBooking != null)
            bookingRepo.update(oldBooking, booking, user);
        else
            bookingRepo.insert(booking, user);
        user.setBalance(user.getBalance() - cost);
    }

    @Override
    public void deleteUser(User user) throws DatabaseFailedException, InvalidIdException {
        userRepo.delete(user);
    }

    @Override
    public List<Booking> retrieveBookings(User user) throws InvalidIdException {
        return bookingRepo.get(user);
    }

    @Override
    public void deleteBooking(@NotNull Booking booking, @NotNull User user) throws DatabaseFailedException, InvalidIdException {
        bookingRepo.delete(booking, user);
    }

    @Override
    public Hall retrieveShowTimeHall(@NotNull ShowTime showTime) throws InvalidIdException {
        return hallRepo.get(showTime);
    }



}
