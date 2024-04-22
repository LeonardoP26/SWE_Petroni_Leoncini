package domain;

import business_logic.Subject;
import business_logic.exceptions.DatabaseFailedException;
import business_logic.exceptions.NotEnoughFundsException;
import business_logic.repositories.UserRepository;
import business_logic.repositories.UserRepositoryInterface;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class User extends Subject<User> implements DatabaseEntity {

    public static String USER_ID = "user_id";

    public User(ResultSet res) throws SQLException {
        this.id = res.getInt("user_id");
        this.username = res.getString("username");
        this.password = res.getString("password");
        this.balance = res.getLong("balance");
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        addObserver(UserRepository.getInstance());
    }

    public User(String username, String password, UserRepositoryInterface userRepo) {
        this.username = username;
        this.password = password;
        addObserver(userRepo);
    }

    private User(int id, String username, String password, long balance, List<Booking> bookings){
        this.id = id;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.bookings = bookings;
    }


    private int id = ENTITY_WITHOUT_ID;
    private String username;
    private String password;
    private long balance = 0;
    private List<Booking> bookings = new ArrayList<>();



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getName() { return getUsername(); }

    public List<Booking> getBookings() { return bookings; }

    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public int getId() { return id; }

    public long getBalance() { return balance; }

    public String getPassword() { return password; }

    public void setBalance(long balance) throws NotEnoughFundsException, DatabaseFailedException {
        if(balance < 0)
            throw new NotEnoughFundsException("You do not have enough money, please recharge your account.");
        notifyObservers(new User(id, username, password, balance, bookings));
        this.balance = balance;
    }

    public void setId(@NotNull ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("user_id");
    }

}
