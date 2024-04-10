package BusinessLogic.services;

import BusinessLogic.exceptions.*;
import BusinessLogic.repositories.*;
import Domain.*;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
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

    private String encryptPassword(@NotNull String password) throws NoSuchAlgorithmException {
        byte[] rawPassword = MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(rawPassword);
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
    public void addHall(@NotNull Hall hall, @NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException, InvalidIdException {
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This cinema must be in the database before adding halls to it.");
        int hallId = hallRepo.insert(hall, cinema.getId());
        hall.setId(hallId);
        cinema.setHalls(cinema.getHalls() == null ? List.of(hall) : Stream.concat(cinema.getHalls().stream(), Stream.of(hall)).toList());

    }

    @Override
    public void addSeat(@NotNull Seat seat, @NotNull Hall hall) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException, InvalidIdException {
        if(hall.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("This hall must be in the database before adding seats to it.");
        int seatId = seatsRepo.insert(seat, hall.getId());
        seat.setId(seatId);
        hall.setSeats(hall.getSeats() == null ? List.of(seat) : Stream.concat(hall.getSeats().stream(), Stream.of(seat)).toList());
    }

    /**
     *
     * Method to add a movie into the database.
     *
     * @param movie the movie to add into the database;
     */

    @Override
    public void addMovie(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException {
        int movieId = movieRepo.insert(movie);
        movie.setId(movieId);
    }

    @Override
    public void addShowTime(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException {
            int showTimeId = showTimeRepo.insert(showTime);
            showTime.setId(showTimeId);
            for (Seat seat : showTime.getHall().getSeats()) {
                showTimeRepo.insertShowTimeSeat(showTimeId, seat.getId());
            }
    }

    /**
     *
     * Method to add a movie already in the database to a cinema creating a new {@link ShowTime#ShowTime(Movie, Hall, LocalDateTime) ShowTime}
     *
     * @param movie the movie to show
     * @param cinema in which cinema the movie's show will take place
     * @param hall in which hall the movie's show will take place, must be a hall of the cinema
     * @param date when the movie's show will start
     * @throws DatabaseInsertionFailedException if the hall does not belong to this cinema.
     * @throws InvalidIdException if cinema id and hall id are equal to {@link DatabaseEntity#ENTITY_WITHOUT_ID}
     */
    @Override
    public void addMovie(@NotNull Movie movie, @NotNull Cinema cinema, @NotNull Hall hall, LocalDateTime date) throws DatabaseInsertionFailedException, SQLException, UnableToOpenDatabaseException, InvalidIdException {
        if(cinema.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("You need to add this cinema in the database before planning a movie show.");
        if (hall.getId() == DatabaseEntity.ENTITY_WITHOUT_ID)
            throw new InvalidIdException("You need to add this hall in the database before planning a movie show.");
        if(cinema.getHalls().stream().noneMatch(h -> h.getId() == hall.getId()))
            throw new DatabaseInsertionFailedException("The hall does not belong to this cinema.");
        ShowTime sht = new ShowTime(movie, hall, date);
        addShowTime(sht);
        cinema.setMovies(Stream.concat(cinema.getMovies().stream() ,Stream.of(movie)).toList());
    }

    @Override
    public void addCinema(@NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException {
        int cinemaId = cinemaRepo.insert(cinema);
        cinema.setId(cinemaId);
    }

    @Override
    public void addUser(@NotNull User user) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException {
        int userId = userRepo.insert(user);
        user.setId(userId);
    }

    @Override
    public void addBooking(@NotNull Booking booking, List<User> users) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException {
        int bookingNumber = bookingRepo.insert(booking, users);
        booking.setBookingNumber(bookingNumber);
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
    public void retrieveCinemaMovies(@NotNull Cinema cinema){
        try{
            cinema.setMovies(movieRepo.get(cinema));
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public List<ShowTime> retrieveMovieShowTimes(@NotNull Movie movie) {
        try{
            return showTimeRepo.get(movie);
        } catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public void retrieveShowTimeHallSeats(@NotNull ShowTime showTime) {
        try{
            showTime.getHall().setSeats(seatsRepo.get(showTime));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public User login(String username, String password) {
        try{
            return userRepo.get(username, encryptPassword(password));
        } catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public User register(String username, String password){
        try{
            User newUser = new User(username, encryptPassword(password));
            addUser(newUser);
            return newUser;
        } catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public User retrieveUser(String username){
        try{
            return userRepo.get(username);
        } catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean rechargeAccount(User user, long amount) {
        try{
            return userRepo.update(user, amount);
        } catch(Exception e){
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean pay(@NotNull Booking booking, @NotNull User owner, List<User> others) throws NotEnoughFundsException, InvalidSeatException {
        try {
            int totalSpending = booking.getSeats().size() * booking.getShowTime().getHall().getCost();
            if(booking.getSeats().stream().anyMatch(Seat::isBooked))
                throw new InvalidSeatException("Some of these seats are already taken.");
            owner.setBalance(owner.getBalance() - totalSpending);
            addBooking(booking, Stream.concat(others.stream(), Stream.of(owner)).toList());
            for (Seat seat : booking.getSeats()){
                showTimeRepo.updateShowTimeSeat(booking.getShowTime(), seat, booking.getBookingNumber());
            }
            return true;
        } catch (SQLException | UnableToOpenDatabaseException | DatabaseInsertionFailedException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteUser(User user){
        try{
            return userRepo.delete(user);
        } catch (Exception e){
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public List<Booking> retrieveBookings(User user) {
        try{
            return bookingRepo.get(user);
        } catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean deleteBooking(@NotNull Booking booking){
        try{
            return bookingRepo.delete(booking);
        } catch (Exception e){
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Hall retrieveShowTimeHall(@NotNull ShowTime showTime){
        try{
            return hallRepo.get(showTime);
        } catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }

}
