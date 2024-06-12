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

public interface CinemaService {

    void addHall(@NotNull Hall hall) throws DatabaseFailedException, InvalidIdException;

    void addSeat(@NotNull Seat seat, @NotNull Hall hall) throws DatabaseFailedException, InvalidIdException;

    void addMovie(@NotNull Movie movie) throws DatabaseFailedException;

    void addShowTime(@NotNull ShowTime showTime) throws DatabaseFailedException, InvalidIdException;

    void addMovie(@NotNull Movie movie, @NotNull Hall hall, LocalDateTime date) throws DatabaseFailedException, InvalidIdException;

    void addCinema(@NotNull Cinema cinema) throws DatabaseFailedException;

    void addUser(@NotNull User user) throws DatabaseFailedException;

    List<Cinema> retrieveCinemas();

    List<Movie> retrieveCinemaMovies(@NotNull Cinema cinema) throws InvalidIdException;

    List<ShowTime> retrieveMovieShowTimes(@NotNull Movie movie, @NotNull Cinema cinema) throws InvalidIdException, DatabaseFailedException;

    List<Seat> retrieveShowTimeHallSeats(@NotNull ShowTime showTime) throws InvalidIdException;

    User login(String username, String password);

    User register(String username, String password) throws DatabaseFailedException;

    void rechargeAccount(User user, long amount) throws NotEnoughFundsException, DatabaseFailedException, InvalidIdException;

    void pay(@NotNull Booking booking, @Nullable Booking oldBooking, @NotNull User owner) throws NotEnoughFundsException, InvalidSeatException, DatabaseFailedException, InvalidIdException;

    void updateUser(@NotNull User user, @NotNull String newUsername, @NotNull String newPassword) throws DatabaseFailedException, InvalidIdException;

    void deleteUser(User user) throws DatabaseFailedException, InvalidIdException;

    List<Booking> retrieveBookings(User user) throws InvalidIdException;

    void deleteBooking(@NotNull Booking booking, @NotNull User user) throws DatabaseFailedException, InvalidIdException;

}
