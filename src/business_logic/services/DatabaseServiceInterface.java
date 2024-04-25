package business_logic.services;

import business_logic.exceptions.*;
import domain.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public interface DatabaseServiceInterface {

    void addHall(@NotNull Hall hall, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException;

    void addSeat(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException;

    void addMovie(@NotNull Movie movie) throws DatabaseFailedException;

    void addShowTime(@NotNull ShowTime showTime) throws DatabaseFailedException, InvalidIdException;

    void addMovie(@NotNull Movie movie, @NotNull Cinema cinema, @NotNull Hall hall, LocalDateTime date) throws DatabaseFailedException, InvalidIdException;

    void addCinema(@NotNull Cinema cinema) throws DatabaseFailedException;

    void addUser(@NotNull User user) throws DatabaseFailedException;

    void addBooking(@NotNull Booking booking, User user) throws DatabaseFailedException, InvalidIdException;

    List<Cinema> retrieveCinemas();

    List<Movie> retrieveCinemaMovies(@NotNull Cinema cinema);

    List<ShowTime> retrieveMovieShowTimes(@NotNull Movie movie);

    List<Seat> retrieveShowTimeHallSeats(@NotNull ShowTime showTime);

    User login(String username, String password);

    User register(String username, String password) throws DatabaseFailedException;

    User retrieveUser(String username);

    void rechargeAccount(User user, long amount) throws NotEnoughFundsException, DatabaseFailedException;

    boolean pay(@NotNull Booking booking, @NotNull User owner, long cost) throws NotEnoughFundsException, InvalidSeatException, DatabaseFailedException;

    void deleteUser(User user) throws DatabaseFailedException;

    List<Booking> retrieveBookings(User user);

    void deleteBooking(@NotNull Booking booking) throws DatabaseFailedException;

    Hall retrieveShowTimeHall(@NotNull ShowTime showTime);
}
