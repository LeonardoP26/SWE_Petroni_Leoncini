package business_logic.services;

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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CinemaServiceImpl implements CinemaService {

    private static CinemaService instance = null;
    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;
    private final SeatRepository seatRepo;
    private final ShowTimeRepository showTimeRepo;
    private final MovieRepository movieRepo;
    private final HallRepository hallRepo;
    private final CinemaRepository cinemaRepo;

    public static CinemaService getInstance(
            CinemaRepository cinemaRepo,
            HallRepository hallRepo,
            MovieRepository movieRepo,
            ShowTimeRepository showTimeRepo,
            SeatRepository seatRepo,
            UserRepository userRepo,
            BookingRepository bookingRepo
    ){
        if(instance == null)
            instance = new CinemaServiceImpl(cinemaRepo, hallRepo, movieRepo, showTimeRepo, seatRepo, userRepo, bookingRepo);
        return instance;
    }

    public static CinemaService getInstance(){
        if(instance == null)
            instance = new CinemaServiceImpl();
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


    private CinemaServiceImpl(
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
    }

    private CinemaServiceImpl() {
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
    public void addHall(@NotNull Hall hall) throws DatabaseFailedException, InvalidIdException {
        hallRepo.insert(hall);
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
     * @param hall in which hall the movie's show will take place, must be a hall of the cinema
     * @param date when the movie's show will start
     * @throws DatabaseFailedException if the hall does not belong to this cinema.
     * @throws InvalidIdException if cinema id and hall id are equal to {@link DatabaseEntity#ENTITY_WITHOUT_ID ENTITY_WITHOUT_ID}
     */
    @Override
    public void addMovie(@NotNull Movie movie, @NotNull Hall hall, LocalDateTime date) throws DatabaseFailedException, InvalidIdException {
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
    public List<Cinema> retrieveCinemas() {
        return cinemaRepo.get();
    }

    @Override
    public List<Movie> retrieveCinemaMovies(@NotNull Cinema cinema) throws InvalidIdException {
        return movieRepo.get(cinema);
    }

    @Override
    public List<ShowTime> retrieveMovieShowTimes(@NotNull Movie movie, @NotNull Cinema cinema) throws InvalidIdException {
        List<ShowTime> showTimes = showTimeRepo.get(movie, cinema);
        for(ShowTime sht : showTimes){
            sht.setHall(hallRepo.get(sht.getHall()));
        }
        return showTimes;
    }

    @Override
    public List<Seat> retrieveShowTimeHallSeats(@NotNull ShowTime showTime) throws InvalidIdException {
        List<Seat> seats = seatRepo.get(showTime);
        showTime.getHall().setSeats(new ArrayList<>(seats));
        return seats;
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
    public void pay(@NotNull Booking booking, @Nullable Booking oldBooking, @NotNull User user) throws NotEnoughFundsException, InvalidSeatException, DatabaseFailedException, InvalidIdException {
        List<Seat> diverse = new ArrayList<>(booking.getSeats());
        if(oldBooking != null) {
            diverse.removeAll(oldBooking.getSeats());
            if (diverse.stream().anyMatch(Seat::isBooked))
                throw new InvalidSeatException("Some of these seats are already taken.");
            bookingRepo.update(oldBooking, booking, user);
        }
        else {
            if (diverse.stream().anyMatch(Seat::isBooked))
                throw new InvalidSeatException("Some of these seats are already taken.");
            bookingRepo.insert(booking, user);
        }
    }

    @Override
    public void updateUser(@NotNull User user, @NotNull String newUsername, @NotNull String newPassword) throws DatabaseFailedException, InvalidIdException {
        String encryptedPassword = newPassword.equals(user.getPassword()) ? user.getPassword() : encryptPassword(newPassword);
        try {
            userRepo.update(user, (u) -> {
                u.setUsername(newUsername);
                u.setPassword(encryptedPassword);
            });
        } catch (NotEnoughFundsException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUser(User user) throws DatabaseFailedException, InvalidIdException {
        userRepo.delete(user);
    }

    @Override
    public List<Booking> retrieveBookings(User user) throws InvalidIdException {
        List<Booking> bookings = bookingRepo.get(user);
        for (Booking b : bookings){
            ShowTime sht = showTimeRepo.get(b.getShowTime());
            b.setShowTime(sht);
            sht.setHall(hallRepo.get(sht.getHall()));
            sht.setMovie(movieRepo.get(sht.getMovie()));
            sht.getHall().setCinema(cinemaRepo.get(sht.getHall().getCinema()));
            ArrayList<Seat> seats = new ArrayList<>();
            for(Seat s : b.getSeats()){
                seats.add(seatRepo.get(s));
            }
            b.setSeats(seats);
        }
        return bookings;
    }

    @Override
    public void deleteBooking(@NotNull Booking booking, @NotNull User user) throws DatabaseFailedException, InvalidIdException {
        bookingRepo.delete(booking, user);
    }

}
