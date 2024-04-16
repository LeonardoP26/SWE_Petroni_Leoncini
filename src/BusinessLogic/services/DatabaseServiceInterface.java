package BusinessLogic.services;

import BusinessLogic.exceptions.*;
import Domain.*;
import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface DatabaseServiceInterface {

    void addHall(@NotNull Hall hall, @NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException, InvalidIdException;

    void addSeat(@NotNull Seat seat, @NotNull Hall hall) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException, InvalidIdException;

    void addMovie(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    void addShowTime(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    void addMovie(@NotNull Movie movie, @NotNull Cinema cinema, @NotNull Hall hall, LocalDateTime date) throws DatabaseFailedException, SQLException, UnableToOpenDatabaseException, InvalidIdException;

    void addCinema(@NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    void addUser(@NotNull User user) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    void addBooking(@NotNull Booking booking, List<User> users) throws SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    List<Cinema> retrieveCinemas();

    List<Movie> retrieveCinemaMovies(@NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException;

    List<ShowTime> retrieveMovieShowTimes(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException;

    List<Seat> retrieveShowTimeHallSeats(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException;

    User login(String username, String password) throws NoSuchAlgorithmException, SQLException, UnableToOpenDatabaseException;

    User register(String username, String password) throws NoSuchAlgorithmException, SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    User retrieveUser(String username) throws SQLException, UnableToOpenDatabaseException;

    boolean rechargeAccount(User user, long amount) throws NotEnoughFundsException, SQLException, UnableToOpenDatabaseException;

    boolean pay(Booking booking, User owner, List<User> others) throws NotEnoughFundsException, InvalidSeatException, SQLException, UnableToOpenDatabaseException, DatabaseFailedException;

    boolean deleteUser(User user) throws SQLException, UnableToOpenDatabaseException;


    List<Booking> retrieveBookings(User user) throws SQLException, UnableToOpenDatabaseException;

    boolean deleteBooking(@NotNull Booking booking) throws SQLException, UnableToOpenDatabaseException;

    Hall retrieveShowTimeHall(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException;
}
