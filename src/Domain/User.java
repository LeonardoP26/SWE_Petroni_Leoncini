package Domain;


import BusinessLogic.*;
import BusinessLogic.repositories.BookingRepository;
import BusinessLogic.repositories.BookingRepositoryInterface;
import BusinessLogic.repositories.UserRepository;
import org.jetbrains.annotations.Range;

import java.sql.SQLException;
import java.util.List;

public class User extends Subject {

    public User(int id, String username, long balance){
        this(id, username);
        this.balance = balance;
    }

    public User(int id, String username){
        this.id = id;
        this.username = username;
        addObserver(UserRepository.getInstance());
    }

    private final BookingRepositoryInterface bookingRepo = BookingRepository.getInstance();
    private final String username;
    private final int id;
    private long balance = 0;


    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(@Range(from = 0, to = Integer.MAX_VALUE) long balance) throws SQLException, UnableToOpenDatabaseException {
        this.balance = balance;
        notifyObservers(this);
    }

    public void rechargeBalance(@Range(from = 0, to = Integer.MAX_VALUE) long amount) throws SQLException, UnableToOpenDatabaseException {
        balance += amount;
        notifyObservers(this);
    }

    private void checkBalance(@Range(from = 0, to = Integer.MAX_VALUE) long amount) throws NotEnoughFundsException{
        if (amount > balance)
            throw new NotEnoughFundsException("Credit is insufficient.");
    }

    public Booking book(ShowTime showTime, List<Seat> seats, List<User> users) throws NotAvailableSeatsException, SQLException, NotEnoughSeatsException, NotEnoughFundsException, UnableToOpenDatabaseException {
        if (seats.size() < users.size())
            throw new NotEnoughSeatsException("You have not chosen enough seats.");
        int totalSpending = showTime.getHall().getCost() * seats.size();
        checkBalance(totalSpending);
        users.add(this);
        Booking booking = bookingRepo.book(showTime, seats, users);
        setBalance(balance - totalSpending);
        return booking;
    }

}
