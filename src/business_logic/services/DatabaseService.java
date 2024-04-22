package business_logic.services;

import business_logic.CinemaDatabase;
import business_logic.exceptions.*;
import business_logic.repositories.*;
import domain.*;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

public class DatabaseService implements DatabaseServiceInterface {

    private final CinemaRepositoryInterface cinemaRepo;
    private final HallRepositoryInterface hallRepo;
    private final MovieRepositoryInterface movieRepo;
    private final ShowTimeRepositoryInterface showTimeRepo;
    private final SeatsRepositoryInterface seatsRepo;
    private final UserRepositoryInterface userRepo;
    private final BookingRepositoryInterface bookingRepo;
    private static DatabaseService instance = null;

    public static DatabaseService getInstance(CinemaRepositoryInterface cinemaRepo, HallRepositoryInterface hallRepo, MovieRepositoryInterface movieRepo, ShowTimeRepositoryInterface showTimeRepo, SeatsRepositoryInterface seatsRepo, UserRepositoryInterface userRepo, BookingRepositoryInterface bookingRepo){
        if(instance == null)
            instance = new DatabaseService(cinemaRepo, hallRepo, movieRepo, showTimeRepo, seatsRepo, userRepo, bookingRepo);
        return instance;
    }

    public static DatabaseService getInstance(){
        if(instance == null)
            instance = new DatabaseService();
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


    private DatabaseService(CinemaRepositoryInterface cinemaRepo, HallRepositoryInterface hallRepo, MovieRepositoryInterface movieRepo, ShowTimeRepositoryInterface showTimeRepo, SeatsRepositoryInterface seatsRepo, UserRepositoryInterface userRepo, BookingRepositoryInterface bookingRepo){
        this.cinemaRepo = cinemaRepo;
        this.hallRepo = hallRepo;
        this.movieRepo = movieRepo;
        this.showTimeRepo = showTimeRepo;
        this.seatsRepo = seatsRepo;
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
    }

    private DatabaseService() {
        this.cinemaRepo = CinemaRepository.getInstance();
        this.hallRepo = HallRepository.getInstance();
        this.movieRepo = MovieRepository.getInstance();
        this.showTimeRepo = ShowTimeRepository.getInstance();
        this.seatsRepo = SeatsRepository.getInstance();
        this.userRepo = UserRepository.getInstance();
        this.bookingRepo = BookingRepository.getInstance();
    }

    @Override
    public void addHall(@NotNull Hall hall, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException {
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema must be in the database before adding halls to it.");
        hallRepo.insert(hall, cinema.getId());
        cinema.setHalls(cinema.getHalls() == null ? List.of(hall) : Stream.concat(cinema.getHalls().stream(), Stream.of(hall)).toList());

    }

    @Override
    public void addSeat(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException {
        if(hall.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall must be in the database before adding seats to it.");
        seatsRepo.insert(seat, hall.getId());
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
        movieRepo.insert(movie);
    }

    @Override
    public void addShowTime(@NotNull ShowTime showTime) throws DatabaseFailedException {
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
     * @throws InvalidIdException if cinema id and hall id are equal to {@link DatabaseEntity#ENTITY_WITHOUT_ID}
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
        cinemaRepo.insert(cinema);
    }

    @Override
    public void addUser(@NotNull User user) throws DatabaseFailedException {
        userRepo.insert(user);
    }

    @Override
    public void addBooking(@NotNull Booking booking, User user) throws DatabaseFailedException {
        bookingRepo.insert(booking, user);
    }

    @Override
    public List<Cinema> retrieveCinemas() {
        try {
            return cinemaRepo.get();
        } catch(Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public List<Movie> retrieveCinemaMovies(@NotNull Cinema cinema) {
        return movieRepo.get(cinema);
    }

    @Override
    public List<ShowTime> retrieveMovieShowTimes(@NotNull Movie movie) {
        return showTimeRepo.get(movie);
    }

    @Override
    public List<Seat> retrieveShowTimeHallSeats(@NotNull ShowTime showTime) {
        return seatsRepo.get(showTime);
    }

    @Override
    public User login(String username, String password) {
        return userRepo.get(username, encryptPassword(password));
    }

    @Override
    public User register(String username, String password) throws DatabaseFailedException {
        User newUser = new User(username, encryptPassword(password));
        addUser(newUser);
        return newUser;
    }

    @Override
    public User retrieveUser(String username) {
        return userRepo.get(username);
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
                return true;
            });
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
        userRepo.delete(user);
    }

    @Override
    public List<Booking> retrieveBookings(User user) {
        return bookingRepo.get(user);
    }

    @Override
    public void deleteBooking(@NotNull Booking booking) throws DatabaseFailedException {
        bookingRepo.delete(booking);
    }

    @Override
    public Hall retrieveShowTimeHall(@NotNull ShowTime showTime) {
        return hallRepo.get(showTime);
    }



}
