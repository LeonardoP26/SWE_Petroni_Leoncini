import BusinessLogic.CinemaDatabase;
import BusinessLogic.services.DatabaseService;
import Domain.*;
import org.jetbrains.annotations.NotNull;
import ui.InputOutputHandler;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import static ui.InputOutputHandler.*;

public class Main {


    public static void main(String[] args) {
        try {
            populateDatabase();
            InputOutputHandler ui = InputOutputHandler.getInstance();

            Cinema selectedCinema = null;
            Movie selectedMovie = null;
            ShowTime selectedShowTime = null;
            List<Seat> selectedSeats = null;
            User user = null;
            Booking currentBooking;
            Booking bookingManaged = null;
            boolean exit = false;

            System.out.println("Welcome");
            Page currentPage = Page.HOMEPAGE;
            while(!exit) {
                switch(currentPage) {
                    case Page.HOMEPAGE -> {
                        currentPage = ui.homePage(user != null);
                        if (currentPage == null) {
                            exit = true;
                        } else if (currentPage == Page.HOMEPAGE) {
                            user = null;
                        }
                    }
                    case Page.MANAGE_ACCOUNT -> {
                        currentPage = ui.accountManagementPage(user);
                    }
                    case Page.MANAGE_BOOKINGS -> {
                        if (user == null){
                            currentPage = Page.LOGIN;
                            break;
                        }
                        bookingManaged = ui.bookingManagePage(user);
                        currentPage = bookingManaged != null ? Page.EDIT_BOOKINGS : Page.HOMEPAGE;
                    }
                    case Page.EDIT_BOOKINGS -> {
                        if(bookingManaged == null){
                            currentPage = Page.HOMEPAGE;
                            break;
                        }
                        currentPage = ui.editBooking(bookingManaged, user);
                    }
                    case Page.CINEMA_SELECTION -> {
                        selectedCinema = ui.cinemaSelectionPage();
                        currentPage = selectedCinema == null ? Page.HOMEPAGE : Page.MOVIE_SELECTION;
                    }
                    case Page.MOVIE_SELECTION -> {
                        if(selectedCinema == null){
                            currentPage = Page.CINEMA_SELECTION;
                            break;
                        }
                        selectedMovie = ui.movieSelectionPage(selectedCinema);
                        if(selectedMovie != null)
                            currentPage = Page.SHOWTIME_SELECTION;
                        else {
                            selectedCinema = null;
                            currentPage = Page.CINEMA_SELECTION;
                        }
                    }
                    case Page.SHOWTIME_SELECTION -> {
                        if(selectedMovie == null){
                            currentPage = Page.MOVIE_SELECTION;
                            break;
                        }
                        selectedShowTime = ui.showTimeSelectionPage(selectedMovie);
                        if (selectedShowTime != null)
                            currentPage = Page.SEAT_SELECTION;
                        else {
                            selectedMovie = null;
                            currentPage = Page.MOVIE_SELECTION;
                        }
                    }
                    case Page.SEAT_SELECTION -> {
                        if(bookingManaged != null){
                            selectedSeats = ui.seatsSelectionPage(bookingManaged.getShowTime());

                        } else {
                            if (selectedShowTime == null) {
                                currentPage = Page.SHOWTIME_SELECTION;
                                break;
                            }
                            selectedSeats = ui.seatsSelectionPage(selectedShowTime);
                            if (selectedSeats == null)
                                currentPage = Page.SHOWTIME_SELECTION;
                            else if (user == null)
                                currentPage = Page.LOGIN;
                            else
                                currentPage = Page.BOOKING_CONFIRMED;
                        }
                    }
                    case Page.LOGIN -> {
                        user = ui.loginOrRegister();
                        if (user == null && selectedCinema != null) {
                            currentPage = Page.SEAT_SELECTION;
                            selectedSeats = null;
                        } else
                            currentPage = selectedCinema == null ? Page.HOMEPAGE : Page.BOOKING_CONFIRMED;
                    }
                    case Page.BOOKING_CONFIRMED -> {
                        if (user == null){
                            currentPage = Page.LOGIN;
                            break;
                        }
                        if(selectedSeats == null){
                            currentPage = Page.SEAT_SELECTION;
                            break;
                        }
                        List<User> others = ui.addPeopleToBookingPage(selectedSeats.size());
                        currentBooking = new Booking(selectedShowTime, selectedSeats);
                        ui.confirmPaymentPage(currentBooking, user, others);
                        currentPage = Page.HOMEPAGE;
                        selectedCinema = null;
                        selectedMovie = null;
                        selectedShowTime = null;
                        selectedSeats = null;
                        currentBooking = null;
                    }
                }
            }

            System.out.println("Goodbye!");




        } catch (Exception e){
            System.out.println(e.getMessage());
        }
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

    private static void populateDatabase(){
        try {
            if(CinemaDatabase.isDatabaseEmpty()) {
                System.out.println("Populating the database...");
                CinemaDatabase.getConnection();
                DatabaseService databaseService = DatabaseService.getInstance();

                Cinema uci = new Cinema("UCI Cinema");
                Cinema theSpace = new Cinema("The Space");
                databaseService.addCinema(uci);
                databaseService.addCinema(theSpace);

                Hall[] uciHalls = new Hall[]{
                        new Hall(1),
                        new ImaxHall(2),
                        new Imax3DHall(3)
                };

                Hall[] theSpaceHalls = new Hall[]{
                        new Hall(1),
                        new ThreeDHall(2),
                        new ImaxHall(3)
                };

                for (Hall hall : uciHalls) {
                    databaseService.addHall(hall, uci);
                    for (Seat seat : getSomeSeats()) {
                        databaseService.addSeat(seat, hall);
                    }
                }
                for (Hall hall : theSpaceHalls) {
                    databaseService.addHall(hall, theSpace);
                    for (Seat seat : getSomeSeats()) {
                        databaseService.addSeat(seat, hall);
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
                    databaseService.addMovie(movie);
                }

                LocalDateTime showTimeDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(3);
                databaseService.addMovie(movies[0], uci, uci.getHalls().getFirst(), showTimeDay.plusHours(18).plusMinutes(30));
                databaseService.addMovie(movies[1], uci, uci.getHalls().get(1), showTimeDay.plusHours(21));
                databaseService.addMovie(movies[2], uci, uci.getHalls().getLast(), showTimeDay.plusHours(21).plusMinutes(30));
                databaseService.addMovie(movies[3], theSpace, theSpace.getHalls().getFirst(), showTimeDay.plusHours(21).plusMinutes(30));
                databaseService.addMovie(movies[4], theSpace, theSpace.getHalls().get(1), showTimeDay.plusHours(22));
                databaseService.addMovie(movies[5], theSpace, theSpace.getHalls().getLast(), showTimeDay.plusHours(18).plusMinutes(45));

                System.out.println("Finished.");
            }

        } catch (Exception e){
            /*
             * Since we are populating the database i.e.
             * we are setting up the workspace every exception launched
             * in this process should make the program stops.
             */
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

}