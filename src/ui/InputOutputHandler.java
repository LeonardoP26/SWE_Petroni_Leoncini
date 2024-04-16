package ui;

import BusinessLogic.exceptions.DatabaseFailedException;
import BusinessLogic.exceptions.InvalidSeatException;
import BusinessLogic.exceptions.NotEnoughFundsException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import BusinessLogic.services.DatabaseService;
import BusinessLogic.services.DatabaseServiceInterface;
import Domain.*;
import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;

import static ui.InputOutputHandler.Page.*;

public class InputOutputHandler {

    public enum Page {
        HOMEPAGE,
        MANAGE_ACCOUNT,
        MANAGE_BOOKINGS,
        EDIT_BOOKINGS,
        EDIT_ACCOUNT,
        CINEMA_SELECTION,
        MOVIE_SELECTION,
        SHOWTIME_SELECTION,
        SEAT_SELECTION,
        LOGIN_OR_REGISTER,
        BOOKING_CONFIRMED
    }

    private final DatabaseServiceInterface databaseService;
    private static InputOutputHandler instance = null;

    public static InputOutputHandler getInstance(DatabaseServiceInterface databaseService){
        if(instance == null)
            instance = new InputOutputHandler(databaseService);
        return instance;
    }

    public static InputOutputHandler getInstance(){
        if(instance == null)
            instance = new InputOutputHandler();
        return instance;
    }

    private InputOutputHandler(DatabaseServiceInterface databaseService){
        this.databaseService = databaseService;
    }
    private InputOutputHandler(){
        this.databaseService = DatabaseService.getInstance();
    }

    public Page homePage(boolean alreadyLoggedIn){
        int input = 0;
        int choices = alreadyLoggedIn ? 4 : 3;
        while(input < 1 || input > choices) {
            System.out.println("Choose if you want to login or book for a movie:");
            if(alreadyLoggedIn)
                System.out.println("1. Logout\n2. Manage your account\n3. Book\n4. Exit");
            else
                System.out.println("1. Login\n2. Book\n3. Exit");
            try{
                input = readInput();
            } catch (NoSuchElementException | IllegalStateException e){
                input = 0;
            }
            if(input < 1 || input > choices)
                System.out.println("Choose a number between 1 and " + choices);
        }
        return switch(input){
            case 1 -> alreadyLoggedIn ? HOMEPAGE : LOGIN_OR_REGISTER;
            case 2 -> alreadyLoggedIn ? MANAGE_ACCOUNT : CINEMA_SELECTION;
            case 3 -> alreadyLoggedIn ? CINEMA_SELECTION : null;
            default -> null;
        };
    }

    public User loginOrRegisterPage() throws SQLException, UnableToOpenDatabaseException, NoSuchAlgorithmException {
        int input = 0;
        while (input < 1 || input > 3) {
            System.out.println("Do you want to login or register:");
            System.out.println("1. Login\n2. Register\n3. Back");
            try {
                input = readInput();
            } catch (NoSuchElementException | IllegalStateException e) {
                System.out.println("Choose a number between 1 and 3");
                input = 0;
            }
        }
        User user;
        String username = "";
        String password = "";
        while (true) {
            if (input == 1 || input == 2) {
                System.out.print("Insert username or leave it blank to go back:\n>> ");
                Scanner sc = new Scanner(System.in);
                username = sc.nextLine();
                if (username.isBlank())
                    break;
                System.out.print("Insert password:\n>> ");
                sc = new Scanner(System.in);
                password = sc.nextLine();
            }
            switch (input) {
                case 1 -> {
                    user = databaseService.login(username, password);
                    if (user != null)
                        return user;
                    else
                        System.out.println("Username or password are not correct.");
                }
                case 2 -> {
                    try{
                        user = databaseService.register(username, password);
                        if (user != null)
                            return user;
                        else
                            System.out.println("Username or password are not correct.");
                    } catch (DatabaseFailedException e){
                        System.out.println(e.getMessage());
                    }
                }
                case 3 -> {
                    return null;
                }
            }
        }
        return null;
    }


    public Cinema cinemaSelectionPage(){
        List<Cinema> cinemas = databaseService.retrieveCinemas();
        int input = chooseOption(cinemas.stream().map(Cinema::getName).toList(), "Choose a cinema:", "Back");
        if (input == cinemas.size())
            return null;
        return cinemas.get(input);
    }

    public Movie movieSelectionPage(@NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException {
        List<Movie> movies = databaseService.retrieveCinemaMovies(cinema);
        cinema.setMovies(movies);
        int input = chooseOption(movies.stream().map(Movie::getName).toList(), "Choose a movie", "Back");
        if(input == movies.size())
            return null;
        return movies.get(input);
    }

    public ShowTime showTimeSelectionPage(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException {
        List<ShowTime> showTimes = databaseService.retrieveMovieShowTimes(movie);
        int input = chooseOption(showTimes.stream().map(ShowTime::getName).toList(), "Choose a show time", "Back");
        if(input == showTimes.size())
            return null;
        return showTimes.get(input);
    }

    public Booking bookingManagePage(@NotNull User user) throws SQLException, UnableToOpenDatabaseException {
        List<Booking> bookings = databaseService.retrieveBookings(user);
        if(bookings == null)
            bookings = List.of();
        int input = chooseOption(bookings.stream().map(Booking::getName).toList(), "Choose the booking to edit:", "Back");
        if (input == bookings.size())
            return null;
        return bookings.get(input);
    }

    public List<Seat> seatsSelectionPage(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException {
        List<Seat> seats = databaseService.retrieveShowTimeHallSeats(showTime);
        System.out.println("Choose your seats following this pattern \"a1-a2-a3\" or leave it blank to return to the previous page:");
        char row = seats.getFirst().getRow();
        System.out.print(row + "\t\t");
        for (Seat s : seats){
            if(row != s.getRow()) {
                System.out.print("\n" + s.getRow() + "\t\t");
                row = s.getRow();
            }
            if(!s.isBooked())
                System.out.print(s.getNumber() + "\t");
            else
                System.out.print("X\t");
        }
        System.out.println();
        List<String> strings = new ArrayList<>();
        boolean inputNotValid = true;
        while(inputNotValid){
            System.out.print(">> ");
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine();
            if(input.isBlank()){
                return null;
            }
            while (!input.isEmpty()){
                if(!Character.isDigit(input.charAt(input.length() - 1)))
                    break;
                String subString;
                if(input.indexOf('-') != -1) {
                    subString = input.substring(0, input.indexOf('-'));
                    input = input.replace(subString + "-", "");
                } else {
                    subString = input;
                    input = "";
                }
                if(seats.stream().noneMatch(s -> s.getRow() == subString.charAt(0)))
                    break;
                try{
                    int number = Integer.parseInt(subString.substring(1));
                    if (seats.stream().noneMatch(s -> s.getNumber() == number))
                        break;
                } catch (NumberFormatException e){
                    break;
                }
                strings.add(subString);
            }
            inputNotValid = false;
        }
        return seats.stream().filter(s ->
                strings.stream().anyMatch(c ->
                        s.getRow() == c.charAt(0) &&
                        String.valueOf(s.getNumber()).equals(c.substring(1))
                )
        ).toList();
    }

    public List<User> addPeopleToBookingPage(int max) throws SQLException, UnableToOpenDatabaseException {
        List<User> users = new ArrayList<>(max);
        System.out.println("Add people's usernames to this booking or leave it blank to finish:");
        while (users.size() < max){
            System.out.print(">>");
            Scanner sc = new Scanner(System.in);
            String username = sc.nextLine();
            if(username.isBlank())
                return users;
            User user = databaseService.retrieveUser(username);
            if(user != null) {
                users.add(user);
            } else {
                System.out.println("User does not exist.");
            }
        }
        return users;
    }

    public boolean confirmPaymentPage(Booking booking, User owner, List<User> others) throws NotEnoughFundsException, SQLException, UnableToOpenDatabaseException {
        int cost = booking.getShowTime().getHall().getCost() * booking.getSeats().size();
        System.out.println("Confirm the booking? Cost: " + cost + " - Your balance: " + owner.getBalance() + " \n1. Yes\n2. No");
        int input = 0;
        while (input < 1 || input > 2) {
            try{
                input = readInput();
            } catch (NoSuchElementException | IllegalStateException e){
                System.out.println("Choose a number between 1 and 2");
                input = 0;
            }
        }
        if (input == 1) {
            try {
                return databaseService.pay(booking, owner, others);
            } catch (NotEnoughFundsException e) {
                System.err.println(e.getMessage());
                System.out.println("Do you want to recharge your account?\n1. Yes\n2. No");
                int input1 = 0;
                while (input1 < 1 || input1 > 2) {
                    try{
                        input1 = readInput();
                    } catch (NoSuchElementException | IllegalStateException ex){
                        System.out.println("Choose a number between 1 and 2");
                        input1 = 0;
                    }
                }
                if (input1 == 1)
                    if (rechargeAccount(owner))
                        return confirmPaymentPage(booking, owner, others);
                return false;
            } catch(InvalidSeatException | DatabaseFailedException e){
                System.out.println(e.getMessage());
                return false;
            }
        } else return false;
    }

    public boolean rechargeAccount(User user) throws NotEnoughFundsException, SQLException, UnableToOpenDatabaseException {
        System.out.println("How much you would like to charge?");
        long input = 0;
        while (input < 1){
            input = readInput();
        }
        if(!databaseService.rechargeAccount(user, input)){
            System.out.println("Recharge failed. Do you want to try again?\n1. Yes\n2. No");
            int input1 = 0;
            while(input1 < 1 || input1 > 2){
                input1 = readInput();
            }
            if(input1 == 1)
                rechargeAccount(user);
            else return false;
        }
        return true;
    }

    private int chooseOption(List<String> options, String title, String back){
        System.out.println(title);
        int i = 0;
        while(i < options.size()){
            System.out.println((i + 1) + ". " + options.get(i));
            i++;
        }
        System.out.println((i + 1) + ". " + back);
        int choice = -1;
        while(choice < 0 || choice > options.size() + 1)
            try{
                choice = readInput() - 1;
            } catch (NoSuchElementException | IllegalStateException e){
                System.out.println("Choose a number between 1 and " + (options.size() + 1));
                choice = -1;
            }
        return choice;
    }

    private int readInput() throws NoSuchElementException, IllegalStateException {
        System.out.print(">> ");
        Scanner sc = new Scanner(System.in);
        return sc.nextInt();
    }

    public Page accountManagementPage(User user) throws SQLException, UnableToOpenDatabaseException {
        System.out.println("What would you like to do?\n1. Edit bookings\n2. Edit my personal infos\n3. Delete my account\n4. Back");
        int input = 0;
        while(input < 1 || input > 4){
            input = readInput();
        }
        return switch(input){
            case 1 -> MANAGE_BOOKINGS;
            case 2 -> EDIT_ACCOUNT;
            case 3 -> {
                if(databaseService.deleteUser(user))
                    user = null;
                yield HOMEPAGE;
            }
            default -> HOMEPAGE;
        };
    }

    public Page editBooking(@NotNull Booking booking, User user) throws SQLException, UnableToOpenDatabaseException, NotEnoughFundsException {
        System.out.println("What would you like to do?\n1. Change seats\n2. Delete this booking\n3. Back");
        int input = 0;
        while(input < 1 || input > 3){
            input = readInput();
        }
        return switch(input){
            case 1 -> {
                if(booking.getShowTime().getHall() == null){
                    databaseService.retrieveShowTimeHall(booking.getShowTime());
                }
                if(booking.getShowTime().getHall().getSeats() == null){
                    databaseService.retrieveShowTimeHallSeats(booking.getShowTime());
                }
                yield SEAT_SELECTION;
            }
            case 2 -> {
                if(booking.getShowTime().getHall() == null){
                    databaseService.retrieveShowTimeHall(booking.getShowTime());
                }
                if (databaseService.deleteBooking(booking)) {
                    long refund = (long) booking.getShowTime().getHall().getCost() * booking.getSeats().size();
                    databaseService.rechargeAccount(user, refund);
                    yield HOMEPAGE;
                }
                else yield EDIT_BOOKINGS;
            }
            default -> MANAGE_BOOKINGS;
        };

    }
}
