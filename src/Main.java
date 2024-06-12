import business_logic.CinemaDatabase;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.services.CinemaService;
import business_logic.services.CinemaServiceImpl;
import domain.*;
import org.jetbrains.annotations.NotNull;
import ui.InputOutputHandler;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static ui.InputOutputHandler.Page;

public class Main {


    public static void main(String[] args) throws DatabaseFailedException, InvalidIdException {

        populateDatabase();
        InputOutputHandler ui = InputOutputHandler.getInstance();

        Cinema selectedCinema = null;
        Movie selectedMovie = null;
        ShowTime selectedShowTime = null;
        List<Seat> selectedSeats = null;
        User user = null;
        Booking userBooking = null;
        boolean exit = false;

        Page currentPage = Page.HOMEPAGE;

        System.out.println("Welcome");
        while (!exit) {
            switch (currentPage) {
                case Page.HOMEPAGE -> {
                    currentPage = ui.homePage(user != null);
                    if (currentPage == Page.HOMEPAGE && user != null)
                        user = null;
                    else if (currentPage == null) {
                        exit = true;
                    }
                }
                case LOGIN_OR_REGISTER -> {
                    user = ui.loginOrRegisterPage();
                    currentPage = selectedSeats == null ? Page.HOMEPAGE : Page.BOOKING_CONFIRMED;
                }
                case Page.MANAGE_ACCOUNT -> {
                    currentPage = ui.accountManagementPage(user);
                }
                case Page.EDIT_ACCOUNT -> {
                    assert user != null;
                    currentPage = ui.editAccount(user);
                }
                case Page.DELETE_ACCOUNT -> {
                    user = null;
                    currentPage = Page.HOMEPAGE;
                }
                case Page.MANAGE_BOOKINGS -> {
                    assert user != null;
                    userBooking = ui.bookingManagePage(user);
                    currentPage = userBooking != null ? Page.EDIT_BOOKINGS : Page.MANAGE_ACCOUNT;
                }
                case Page.EDIT_BOOKINGS -> {
                    assert userBooking != null;
                    currentPage = ui.editBooking(userBooking, user);
                    if(currentPage == Page.SEAT_SELECTION) {
                        selectedCinema = userBooking.getShowTime().getHall().getCinema();
                        selectedMovie = userBooking.getShowTime().getMovie();
                        selectedShowTime = userBooking.getShowTime();
                        selectedSeats = userBooking.getSeats();
                    }
                }
                case DELETE_BOOKING -> {
                    userBooking = null;
                    currentPage = Page.HOMEPAGE;
                }
                case Page.CINEMA_SELECTION -> {
                    selectedCinema = ui.cinemaSelectionPage();
                    currentPage = selectedCinema == null ? Page.HOMEPAGE : Page.MOVIE_SELECTION;
                }
                case MOVIE_SELECTION -> {
                    assert selectedCinema != null;
                    selectedMovie = ui.movieSelectionPage(selectedCinema);
                    currentPage = selectedMovie == null ? Page.CINEMA_SELECTION : Page.SHOWTIME_SELECTION;
                }
                case SHOWTIME_SELECTION -> {
                    assert selectedMovie != null;
                    selectedShowTime = ui.showTimeSelectionPage(selectedMovie, selectedCinema);
                    currentPage = selectedShowTime == null ? Page.MOVIE_SELECTION : Page.SEAT_SELECTION;
                }
                case SEAT_SELECTION -> {
                    assert selectedShowTime != null;
                    List<Seat> oldSeats = selectedSeats;
                    selectedSeats = ui.seatsSelectionPage(selectedShowTime, userBooking);
                    if(oldSeats != null && selectedSeats == null) {
                        currentPage = Page.EDIT_BOOKINGS;
                        selectedCinema = null;
                        selectedMovie = null;
                        selectedShowTime = null;
                    }
                    else
                        currentPage = selectedSeats == null ? Page.SHOWTIME_SELECTION : Page.BOOKING_CONFIRMED;
                }
                case BOOKING_CONFIRMED -> {
                    if(user == null){
                        currentPage = Page.LOGIN_OR_REGISTER;
                    }
                    else {
                        assert selectedSeats != null;
                        Booking booking = new Booking(selectedShowTime, new ArrayList<>(selectedSeats));
                        boolean success = ui.confirmPaymentPage(booking, user, userBooking);
                        if(success)
                            System.out.println("Booking confirmed!");
                        else
                            System.out.println("Booking failed. Try again.");
                        selectedCinema = null;
                        selectedMovie = null;
                        selectedShowTime = null;
                        selectedSeats = null;
                        currentPage = Page.HOMEPAGE;
                    }
                }
            }
        }

        System.out.println("Goodbye!");



    }

    private static @NotNull List<Seat> getSomeSeats(){
        List<Seat> seats = new ArrayList<>();
        for(char row = 'a'; row < 'g'; row++){
            for(int num = 1; num < 21; num++){
                if(row == 'e' && num > 18)
                    continue;
                if(row == 'f' && num > 15)
                    continue;
                Seat s = new Seat(row, num);
                seats.add(s);
            }
        }
        return seats;
    }

    private static void populateDatabase() throws DatabaseFailedException, InvalidIdException {
        try {
            if(CinemaDatabase.isDatabaseEmpty()) {
                System.out.println("Populating the database...");
                CinemaDatabase.getConnection(CinemaDatabase.DB_URL);
                CinemaService cinemaService = CinemaServiceImpl.getInstance();

                Cinema uci = new Cinema("UCI Cinema");
                Cinema theSpace = new Cinema("The Space");
                cinemaService.addCinema(uci);
                cinemaService.addCinema(theSpace);

                Hall[] uciHalls = new Hall[]{
                        new Hall(1, uci),
                        new ImaxHall(2, uci),
                        new Imax3DHall(3, uci)
                };

                Hall[] theSpaceHalls = new Hall[]{
                        new Hall(1, theSpace),
                        new ThreeDHall(2, theSpace),
                        new ImaxHall(3, theSpace)
                };

                for (Hall hall : uciHalls) {
                    cinemaService.addHall(hall);
                    for (Seat seat : getSomeSeats()) {
                        cinemaService.addSeat(seat, hall);
                    }
                }
                for (Hall hall : theSpaceHalls) {
                    cinemaService.addHall(hall);
                    for (Seat seat : getSomeSeats()) {
                        cinemaService.addSeat(seat, hall);
                    }
                }

                Movie[] movies = new Movie[]{
                        new Movie("Kung Fu Panda 4", Duration.of(94, ChronoUnit.MINUTES)),
                        new Movie("Dune Part 2", Duration.of(165, ChronoUnit.MINUTES)),
                        new Movie("Past Lives", Duration.of(106, ChronoUnit.MINUTES)),
                        new Movie("The Shawshank Redemption", Duration.of(142, ChronoUnit.MINUTES)),
                        new Movie("Inception", Duration.of(148, ChronoUnit.MINUTES)),
                        new Movie("Pulp Fiction", Duration.of(154, ChronoUnit.MINUTES))
                };
                for (Movie movie : movies) {
                    cinemaService.addMovie(movie);
                }

                LocalDateTime showTimeDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(3);
                cinemaService.addMovie(movies[0], uciHalls[0], showTimeDay.plusHours(18).plusMinutes(30));
                cinemaService.addMovie(movies[1], uciHalls[1], showTimeDay.plusHours(21));
                cinemaService.addMovie(movies[2], uciHalls[2], showTimeDay.plusHours(21).plusMinutes(30));
                cinemaService.addMovie(movies[3], theSpaceHalls[0], showTimeDay.plusHours(21).plusMinutes(30));
                cinemaService.addMovie(movies[4], theSpaceHalls[1], showTimeDay.plusHours(22));
                cinemaService.addMovie(movies[5], theSpaceHalls[2], showTimeDay.plusHours(18).plusMinutes(45));

                System.out.println("Finished.");
            }

        } catch (SQLException e){
            throw new RuntimeException(e);
            /*
             * Since we are populating the database i.e.
             * we are setting up the workspace every exception launched
             * in this process should make the program stops.
             */
//            System.out.println(e.getMessage());
//            System.exit(-1);
        }
    }

}