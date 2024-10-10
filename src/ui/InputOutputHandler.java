package ui;

import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.InvalidIdException;
import business_logic.exceptions.InvalidSeatException;
import business_logic.exceptions.NotEnoughFundsException;
import business_logic.services.CinemaService;
import business_logic.services.CinemaServiceImpl;
import domain.*;
import org.jetbrains.annotations.NotNull;

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

    private final CinemaService cinemaService;
    private static InputOutputHandler instance = null;

    public static InputOutputHandler getInstance(CinemaService cinemaService){
        if(instance == null)
            instance = new InputOutputHandler(cinemaService);
        return instance;
    }

    public static InputOutputHandler getInstance(){
        if(instance == null)
            instance = new InputOutputHandler();
        return instance;
    }

    private InputOutputHandler(CinemaService cinemaService){
        this.cinemaService = cinemaService;
    }
    private InputOutputHandler(){
        this.cinemaService = CinemaServiceImpl.getInstance();
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
                    user = cinemaService.login(username, password);
                    if (user != null)
                        return user;
                    else
                        System.out.println("Username or password are not correct.");
                }
                case 2 -> {
                    try{
                        user = cinemaService.register(username, password);
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
        List<Cinema> cinemas = cinemaService.retrieveCinemas();
        int input = chooseOption(cinemas.stream().map(Cinema::getName).toList(), "Choose a cinema:");
        if (input == cinemas.size())
            return null;
        return cinemas.get(input);
    }

    public Movie movieSelectionPage(@NotNull Cinema cinema) {
        try {
            List<Movie> movies = cinemaService.retrieveCinemaMovies(cinema);
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
            List<ShowTime> showTimes = cinemaService.retrieveMovieShowTimes(movie, cinema);
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
            List<Booking> bookings = cinemaService.retrieveBookings(user);
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
            List<Seat> seats = cinemaService.retrieveShowTimeHallSeats(showTime);
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
            List<Seat> selectedSeats = new ArrayList<>();
            boolean inputNotValid = true;
            while (inputNotValid) {
                System.out.print(">> ");
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                if (input.isBlank()) {
                    return null;
                }
                for (String token : input.split("-")) {
                    if (!token.matches("[a-z]([0-9]+)"))
                        break;
                    List<Seat> rowSeat = seats.stream().filter(s -> s.getRow() == token.charAt(0)).toList();
                    if (rowSeat.isEmpty())
                        break;
                    try {
                        int number = Integer.parseInt(token.substring(1));
                        Seat selectedSeat = rowSeat.stream().filter(s -> s.getNumber() == number).toList().getFirst();
                        selectedSeats.add(selectedSeat);
                    } catch (NumberFormatException | NoSuchElementException e) {
                        break;
                    }
                    inputNotValid = false;
                }
            }
            return selectedSeats;
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
                cinemaService.pay(booking, oldBooking, owner);
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
            cinemaService.rechargeAccount(user, input);
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
            System.out.println(e.getMessage());
            return false;
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
                    cinemaService.deleteUser(user);
                    yield DELETE_ACCOUNT;
                } catch (DatabaseFailedException | InvalidIdException e) {
                    System.out.println(e.getMessage());
                    yield HOMEPAGE;
                }
            }
            default -> HOMEPAGE;
        };
    }

    public Page editAccount(@NotNull User user){
        System.out.println("What do you want to change?\n1. Username\n2. Password\n3. Charge account\n4. Back");
        int maxChoices = 4;
        int input;
        while(true){
            try{
                input = readInput(maxChoices);
                break;
            } catch (NoSuchElementException | IllegalStateException e){
                System.out.println("Choose a number between 1 and " + maxChoices);
            }
        }
        return switch(input){
            case 1, 2 -> changeUserData(user, input);
            case 3 -> {
                rechargeAccount(user);
                yield EDIT_ACCOUNT;
            }
            case 4 -> MANAGE_ACCOUNT;
            default -> HOMEPAGE;
        };
    }

    private Page changeUserData(@NotNull User user, int _case) {
        while (true) {
            if (_case == 1)
                System.out.print("Choose a new username or leave it blank to go back:\n>> ");
            else
                System.out.print("Choose a new password or leave it blank to go back:\n>> ");
            Scanner sc = new Scanner(System.in);
            String newValue = sc.nextLine();
            if (newValue.isEmpty())
                return editAccount(user);
            try {
                if (_case == 1)
                    cinemaService.updateUser(user, newValue, user.getPassword());
                else
                    cinemaService.updateUser(user, user.getUsername(), newValue);
            } catch (DatabaseFailedException e){
                System.out.println(e.getMessage());
                continue;
            } catch (InvalidIdException e){
                System.out.println(e.getMessage());
                return HOMEPAGE;
            }
            return EDIT_ACCOUNT;
        }
    }


    public Page editBooking(@NotNull Booking booking, User user) {
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
                Hall hall = booking.getShowTime().getHall();
                List<Seat> seats;
                try {
                    seats = cinemaService.retrieveShowTimeHallSeats(booking.getShowTime());
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
                Hall hall = booking.getShowTime().getHall();
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
            cinemaService.deleteBooking(booking, user);
            return true;
        } catch (DatabaseFailedException | InvalidIdException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
