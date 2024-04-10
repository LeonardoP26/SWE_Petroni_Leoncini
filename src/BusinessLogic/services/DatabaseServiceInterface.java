package BusinessLogic.services;

import BusinessLogic.exceptions.*;
import Domain.*;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface DatabaseServiceInterface {

    void addHall(@NotNull Hall hall, @NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException, InvalidIdException;

    void addSeat(@NotNull Seat seat, @NotNull Hall hall) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException, InvalidIdException;

    void addMovie(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException;

    void addShowTime(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException;

    void addMovie(@NotNull Movie movie, @NotNull Cinema cinema, @NotNull Hall hall, LocalDateTime date) throws DatabaseInsertionFailedException, SQLException, UnableToOpenDatabaseException, InvalidIdException;

    void addCinema(@NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException;

    void addUser(@NotNull User user) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException;

    void addBooking(@NotNull Booking booking, List<User> users) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException;

    List<Cinema> retrieveCinemas();

    void retrieveCinemaMovies(@NotNull Cinema cinema);

    List<ShowTime> retrieveMovieShowTimes(@NotNull Movie movie);

    void retrieveShowTimeHallSeats(@NotNull ShowTime showTime);

    User login(String username, String password);

    User register(String username, String password);

    User retrieveUser(String username);

    boolean rechargeAccount(User user, long amount);

    boolean pay(Booking booking, User owner, List<User> others) throws NotEnoughFundsException, InvalidSeatException;

    boolean deleteUser(User user);


    List<Booking> retrieveBookings(User user);

    boolean deleteBooking(@NotNull Booking booking);

    Hall retrieveShowTimeHall(@NotNull ShowTime showTime);
}
