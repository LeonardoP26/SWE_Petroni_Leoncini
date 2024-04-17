package BusinessLogic.services;

import BusinessLogic.exceptions.*;
import Domain.*;
import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface DatabaseServiceInterface {

    void addHall(@NotNull Hall hall, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException;

    void addSeat(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException;

    void addMovie(@NotNull Movie movie) throws DatabaseFailedException;

    void addShowTime(@NotNull ShowTime showTime) throws DatabaseFailedException;

    void addMovie(@NotNull Movie movie, @NotNull Cinema cinema, @NotNull Hall hall, LocalDateTime date) throws DatabaseFailedException, InvalidIdException;

    void addCinema(@NotNull Cinema cinema) throws DatabaseFailedException;

    void addUser(@NotNull User user) throws DatabaseFailedException;

    void addBooking(@NotNull Booking booking, List<User> users) throws DatabaseFailedException;

    List<Cinema> retrieveCinemas();

    List<Movie> retrieveCinemaMovies(@NotNull Cinema cinema);

    List<ShowTime> retrieveMovieShowTimes(@NotNull Movie movie);

    List<Seat> retrieveShowTimeHallSeats(@NotNull ShowTime showTime);

    User login(String username, String password);

    User register(String username, String password) throws DatabaseFailedException;

    User retrieveUser(String username);

    boolean rechargeAccount(User user, long amount) throws NotEnoughFundsException;

    boolean pay(@NotNull Booking booking, @NotNull User owner, List<User> others, long cost) throws NotEnoughFundsException, InvalidSeatException, DatabaseFailedException;

    boolean deleteUser(User user);

    List<Booking> retrieveBookings(User user);

    boolean deleteBooking(@NotNull Booking booking);

    Hall retrieveShowTimeHall(@NotNull ShowTime showTime);
}
