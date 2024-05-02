package business_logic.services;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.exceptions.InvalidSeatException;
import business_logic.exceptions.NotEnoughFundsException;
import domain.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public interface DatabaseService {

    void addHall(@NotNull Hall hall, @NotNull Cinema cinema) throws DatabaseFailedException, InvalidIdException;

    void addSeat(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException;

    void addMovie(@NotNull Movie movie) throws DatabaseFailedException;

    void addShowTime(@NotNull ShowTime showTime) throws DatabaseFailedException, InvalidIdException;

    void addMovie(@NotNull Movie movie, @NotNull Cinema cinema, @NotNull Hall hall, LocalDateTime date) throws DatabaseFailedException, InvalidIdException;

    void addCinema(@NotNull Cinema cinema) throws DatabaseFailedException;

    void addUser(@NotNull User user) throws DatabaseFailedException;

    void addBooking(@NotNull Booking booking, User user) throws DatabaseFailedException, InvalidIdException;

    List<Cinema> retrieveCinemas();

    List<Movie> retrieveCinemaMovies(@NotNull Cinema cinema) throws InvalidIdException;

    List<ShowTime> retrieveMovieShowTimes(@NotNull Movie movie) throws InvalidIdException;

    List<Seat> retrieveShowTimeHallSeats(@NotNull ShowTime showTime) throws InvalidIdException;

    User login(String username, String password);

    User register(String username, String password) throws DatabaseFailedException;

    User retrieveUser(String username);

    void rechargeAccount(User user, long amount) throws NotEnoughFundsException, DatabaseFailedException;

    void pay(@NotNull Booking booking, @Nullable Booking oldBooking, @NotNull User owner, long cost) throws NotEnoughFundsException, InvalidSeatException, DatabaseFailedException;

    void deleteUser(User user) throws DatabaseFailedException, InvalidIdException;

    List<Booking> retrieveBookings(User user) throws InvalidIdException;

    void deleteBooking(@NotNull Booking booking) throws DatabaseFailedException, InvalidIdException;

    Hall retrieveShowTimeHall(@NotNull ShowTime showTime) throws InvalidIdException;
}
