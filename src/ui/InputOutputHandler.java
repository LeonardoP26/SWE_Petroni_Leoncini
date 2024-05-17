package ui;

import business_logic.CinemaDatabase;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.exceptions.InvalidSeatException;
import business_logic.exceptions.NotEnoughFundsException;
import business_logic.services.DatabaseService;
import business_logic.services.DatabaseServiceImpl;
import domain.*;
import org.jetbrains.annotations.NotNull;

import javax.xml.crypto.Data;
import java.util.*;

import static ui.InputOutputHandler.Page.*;

public class InputOutputHandler {

    public enum Page {
        HOMEPAGE,
        MANAGE_ACCOUNT,
        MANAGE_BOOKINGS,
        EDIT_BOOKINGS,
        DELETE_BOOKING,
        EDIT_ACCOUNT,
        DELETE_ACCOUNT,
        CINEMA_SELECTION,
        MOVIE_SELECTION,
        SHOWTIME_SELECTION,
        SEAT_SELECTION,
        LOGIN_OR_REGISTER,
        BOOKING_CONFIRMED
    }

    private final DatabaseService databaseService;
    private static InputOutputHandler instance = null;

    public static InputOutputHandler getInstance(DatabaseService databaseService){
        if(instance == null)
            instance = new InputOutputHandler(databaseService);
        return instance;
    }

    public static InputOutputHandler getInstance(){
        if(instance == null)
            instance = new InputOutputHandler();
        return instance;
    }

    private InputOutputHandler(DatabaseService databaseService){
        this.databaseService = databaseService;
    }
    private InputOutputHandler(){
        this.databaseService = DatabaseServiceImpl.getInstance();
    }

    public Page homePage(boolean alreadyLoggedIn){
        int input;
        int maxChoices = alreadyLoggedIn ? 4 : 3;
        while(true) {
            System.out.println("Choose if you want to login or book for a movie:");
            if(alreadyLoggedIn)
                System.out.println("1. Logout\n2. Manage your account\n3. Book\n4. Exit");
            else
                System.out.println("1. Login\n2. Book\n3. Exit");
            try{
                input = readInput(maxChoices);
                break;
            } catch (NoSuchElementException | IllegalStateException e){
                System.out.println("Choose a number between 1 and " + maxChoices);
            }

        }
        return switch(input){
            case 1 -> alreadyLoggedIn ? HOMEPAGE : LOGIN_OR_REGISTER;
            case 2 -> alreadyLoggedIn ? MANAGE_ACCOUNT : CINEMA_SELECTION;
            case 3 -> alreadyLoggedIn ? CINEMA_SELECTION : null;
            default -> null;
        };
    }

    public User loginOrRegisterPage() {
        int input;
        int maxChoices = 3;
        while (true) {
            System.out.println("Do you want to login or register:");
            System.out.println("1. Login\n2. Register\n3. Back");
            try {
                input = readInput(maxChoices);
                break;
            } catch (NoSuchElementException | IllegalStateException e) {
                System.out.println("Choose a number between 1 and 3");
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
                if(username.length() > 15 && input == 2)
                    System.out.println("Username too long. It must contain 15 characters or less.");
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
        int input = chooseOption(cinemas.stream().map(Cinema::getName).toList(), "Choose a cinema:");
        if (input == cinemas.size())
            return null;
        return cinemas.get(input);
    }

    public Movie movieSelectionPage(@NotNull Cinema cinema) {
        try {
            List<Movie> movies = databaseService.retrieveCinemaMovies(cinema);
            if(movies == null)
                movies = List.of();
            cinema.setMovies(new ArrayList<>(movies));
            int input = chooseOption(movies.stream().map(Movie::getName).toList(), "Choose a movie");
            if (input == movies.size())
                return null;
            return movies.get(input);
        } catch (InvalidIdException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public ShowTime showTimeSelectionPage(@NotNull Movie movie, @NotNull Cinema cinema) {
        try {
            List<ShowTime> showTimes = databaseService.retrieveMovieShowTimes(movie, cinema);
            int input = chooseOption(showTimes.stream().map(ShowTime::getName).toList(), "Choose a show time");
            if (input == showTimes.size())
                return null;
            return showTimes.get(input);
        } catch (InvalidIdException | DatabaseFailedException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Booking bookingManagePage(@NotNull User user) {
        try {
            List<Booking> bookings = databaseService.retrieveBookings(user);
            if (bookings == null)
                bookings = List.of();
            user.setBookings(new ArrayList<>(bookings));
            int input = chooseOption(bookings.stream().map(Booking::getName).toList(), "Choose the booking to edit:");
            if (input == bookings.size())
                return null;
            return bookings.get(input);
        } catch (InvalidIdException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Seat> seatsSelectionPage(@NotNull ShowTime showTime, Booking currentBooking) {
        try {
            List<Seat> seats = databaseService.retrieveShowTimeHallSeats(showTime);
            System.out.println("Choose your seats following this pattern \"a1-a2-a3\" or leave it blank to return to the previous page:");
            if (currentBooking != null)
                System.out.println("Seats signed with \"C\" are your current seats.");
            char row = seats.getFirst().getRow();
            System.out.print(row + "\t\t");
            for (Seat s : seats) {
                if (row != s.getRow()) {
                    System.out.print("\n" + s.getRow() + "\t\t");
                    row = s.getRow();
                }
                if (currentBooking != null && currentBooking.getSeats().stream().anyMatch(cs -> cs.getRow() == s.getRow() && cs.getNumber() == s.getNumber()))
                    System.out.print("C\t");
                else if (!s.isBooked())
                    System.out.print(s.getNumber() + "\t");
                else
                    System.out.print("X\t");
            }
            System.out.println();
            List<String> strings = new ArrayList<>();
            boolean inputNotValid = true;
            while (inputNotValid) {
                System.out.print(">> ");
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                if (input.isBlank()) {
                    return null;
                }
                while (!input.isEmpty()) {
                    if (!Character.isDigit(input.charAt(input.length() - 1)))
                        break;
                    String subString;
                    if (input.indexOf('-') != -1) {
                        subString = input.substring(0, input.indexOf('-'));
                        input = input.replace(subString + "-", "");
                    } else {
                        subString = input;
                        input = "";
                    }
                    if (seats.stream().noneMatch(s -> s.getRow() == subString.charAt(0)))
                        break;
                    try {
                        int number = Integer.parseInt(subString.substring(1));
                        if (seats.stream().noneMatch(s -> s.getNumber() == number))
                            break;
                    } catch (NumberFormatException e) {
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
        } catch (InvalidIdException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean confirmPaymentPage(@NotNull Booking booking, User owner, Booking oldBooking) {
        int cost = booking.getShowTime().getHall().getCost() * booking.getSeats().size();
        if(oldBooking != null)
            cost -= oldBooking.getShowTime().getHall().getCost() * oldBooking.getSeats().size();
        System.out.println("Confirm the booking? Cost: " + cost + " - Your balance: " + owner.getBalance() + " \n1. Yes\n2. No");
        int input;
        int maxChoices = 2;
        while (true) {
            try{
                input = readInput(maxChoices);
                break;
            } catch (NoSuchElementException | IllegalStateException e){
                System.out.println("Choose a number between 1 and 2");
            }
        }
        if (input == 1) {
            try {
                databaseService.pay(booking, oldBooking, owner, cost);
                return true;
            } catch (NotEnoughFundsException e) {
                System.out.println(e.getMessage());
                System.out.println("Do you want to recharge your account?\n1. Yes\n2. No");
                int input1;
                int maxChoices1 = 2;
                while (true) {
                    try{
                        input1 = readInput(maxChoices1);
                        break;
                    } catch (NoSuchElementException | IllegalStateException ex){
                        System.out.println("Choose a number between 1 and 2");
                    }
                }
                if (input1 == 1)
                    if (rechargeAccount(owner))
                        return confirmPaymentPage(booking, owner, oldBooking);
                return false;
            } catch(InvalidSeatException | DatabaseFailedException e){
                System.out.println(e.getMessage());
                return false;
            } catch (Exception e) {
                // Unexpected exception: can't handle it
                throw new RuntimeException(e);
            }
        } else return false;
    }

    public boolean rechargeAccount(User user) {
        System.out.println("How much you would like to charge?");
        long input;
        while (true){
            try{
                input = readInput(Integer.MAX_VALUE);
                break;
            } catch (NoSuchElementException | IllegalStateException e){
                System.out.println("Enter a number greater than 0.");
            }
        }
        try{
            databaseService.rechargeAccount(user, input);
        } catch (DatabaseFailedException e) {
            System.out.println("Recharge failed. Do you want to try again?\n1. Yes\n2. No");
            int input1;
            int maxChoices = 2;
            while (true) {
                try {
                    input1 = readInput(maxChoices);
                    break;
                } catch (NoSuchElementException | IllegalStateException ex) {
                    System.out.println("Choose a number between 1 and " + maxChoices);
                }
            }
            if (input1 == 1)
                rechargeAccount(user);
            else return false;
        } catch (InvalidIdException e){
            // TODO
        } catch (NotEnoughFundsException e) {
            // It won't throw, input will be always > 0
            throw new RuntimeException(e);
        }
        return true;
    }

    private int chooseOption(List<String> options, String title){
        System.out.println(title);
        int i = 0;
        while(i < options.size()){
            System.out.println((i + 1) + ". " + options.get(i));
            i++;
        }
        System.out.println((i + 1) + ". " + "Back");
        int choice;
        while(true)
            try{
                choice = readInput(options.size() + 1) - 1;
                break;
            } catch (NoSuchElementException | IllegalStateException e){
                System.out.println("Choose a number between 1 and " + (options.size() + 1));
            }
        return choice;
    }

    private int readInput(int maxChoices) throws NoSuchElementException, IllegalStateException {
        System.out.print(">> ");
        Scanner sc = new Scanner(System.in);
        int input = sc.nextInt();
        if(input < 1 || input > maxChoices)
            throw new InputMismatchException("Out of bounds");
        return input;
    }

    public Page accountManagementPage(User user) {
        System.out.println("What would you like to do?\n1. Edit bookings\n2. Edit my personal infos\n3. Delete my account\n4. Back");
        int input;
        int maxChoices = 4;
        while(true){
            try{
                input = readInput(maxChoices);
                break;
            } catch (NoSuchElementException | IllegalStateException e){
                System.out.println("Choose a number between 1 and " + maxChoices);
            }
        }
        return switch(input){
            case 1 -> MANAGE_BOOKINGS;
            case 2 -> EDIT_ACCOUNT;
            case 3 -> {
                try {
                    databaseService.deleteUser(user);
                    yield DELETE_ACCOUNT;
                } catch (DatabaseFailedException | InvalidIdException e) {
                    System.out.println(e.getMessage());
                    yield HOMEPAGE;
                }
            }
            default -> HOMEPAGE;
        };
    }

    public Page editBooking(@NotNull Booking booking, User user, @NotNull Cinema cinema) {
        System.out.println("What would you like to do?\n1. Change seats\n2. Delete this booking\n3. Back");
        int maxChoices = 3;
        int input;
        while(true){
            try{
                input = readInput(maxChoices);
                break;
            } catch (NoSuchElementException | IllegalStateException e){
                System.out.println("Choose a number between 1 and 3.");
            }
        }
        return switch(input){
            case 1 -> {
                Hall hall;
                try{
                    hall = databaseService.retrieveShowTimeHall(booking.getShowTime(), cinema);
                } catch (InvalidIdException | DatabaseFailedException e) {
                    System.out.println(e.getMessage());
                    hall = null;
                }
                List<Seat> seats;
                try {
                    seats = databaseService.retrieveShowTimeHallSeats(booking.getShowTime());
                } catch (InvalidIdException e){
                    seats = null;
                }
                if(hall == null || seats == null){
                    System.out.println("The hall or its seats do not exist anymore. Probably the show time has been canceled.");
                    yield HOMEPAGE;
                }
                hall.setSeats(new ArrayList<>(seats));
                booking.getShowTime().setHall(hall);
                yield SEAT_SELECTION;
            }
            case 2 -> {
                Hall hall;
                try{
                    hall = databaseService.retrieveShowTimeHall(booking.getShowTime(), cinema);
                } catch (InvalidIdException | DatabaseFailedException e) {
                    System.out.println(e.getMessage());
                    hall = null;
                }
                if(hall == null) {
                    System.out.println("The hall does not exist anymore. Probably the show time has been canceled.");
                    yield EDIT_BOOKINGS;
                }
                if (refundUser(booking, user)) {
                    yield DELETE_BOOKING;
                }
                else {
                    System.out.println("Deletion failed. Try again.");
                    yield EDIT_BOOKINGS;
                }
            }
            default -> MANAGE_BOOKINGS;
        };

    }


    private boolean refundUser(Booking booking, User user) {
        try {
            CinemaDatabase.withTransaction(() -> {
                long refund = (long) booking.getShowTime().getHall().getCost() * booking.getSeats().size();
                try {
                    databaseService.rechargeAccount(user, user.getBalance() + refund);
                    databaseService.deleteBooking(booking, user);
                } catch (DatabaseFailedException e){
                    System.out.println(e.getMessage());
                }
                catch (NotEnoughFundsException e) {
                    // It won't throw, user balance will be surely positive.
                    throw new RuntimeException(e);
                }
            });
            return true;
        } catch (Exception e) {
            // It won't throw
            throw new RuntimeException(e);
        }
    }

}
