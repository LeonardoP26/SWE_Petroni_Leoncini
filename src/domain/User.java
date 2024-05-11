package domain;

import business_logic.exceptions.NotEnoughFundsException;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User implements DatabaseEntity {

    public User(@NotNull ResultSet res) throws SQLException {
        this(res.getString("username"), res.getString("password"));
        this.id = res.getInt("user_id");
        this.balance = res.getLong("balance");
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(@NotNull User user){
        this.username = user.getUsername();
        this.balance = user.getBalance();
        this.password = user.getPassword();
    }


    private int id = ENTITY_WITHOUT_ID;
    private String username;
    private String password;
    private long balance = 0;
    private ArrayList<Booking> bookings = new ArrayList<>();



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

    public ArrayList<Booking> getBookings() { return bookings; }

    public void setBookings(ArrayList<Booking> bookings) { this.bookings = bookings; }

    @Override
    public int getId() { return id; }

    public long getBalance() { return balance; }

    public String getPassword() { return password; }

    public void setBalance(long balance) throws NotEnoughFundsException {
        if(balance < 0)
            throw new NotEnoughFundsException("You do not have enough money, please recharge your account.");
        this.balance = balance;
    }

    public void setId(@NotNull ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("user_id");
    }

    public void copy(@NotNull User user){
        this.username = user.getUsername();
        this.balance = user.getBalance();
        this.password = user.getPassword();
    }

    @Override
    public void resetId() {
        this.id = ENTITY_WITHOUT_ID;
    }

}
