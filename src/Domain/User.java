package Domain;

import BusinessLogic.exceptions.NotEnoughFundsException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class User implements DatabaseEntity {

    public User(ResultSet res) throws SQLException {
        try {
            this.id = res.getInt("Users.id");
            this.username = res.getString("Users.username");
            this.password = res.getString("Users.password");
            this.balance = res.getLong("users.balance");
        } catch (SQLException e){
            this.id = res.getInt("id");
            this.username = res.getString("username");
            this.password = res.getString("password");
            this.balance = res.getLong("balance");
        }
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, long balance) {
        this(username, password);
        this.balance = balance;
    }


    private int id = ENTITY_WITHOUT_ID;
    private String username;
    private String password = null;
    private long balance = 0;
    private List<Booking> bookings = new ArrayList<>();


    public String getUsername() {
        return username;
    }

    @Override
    public String getName() { return getUsername(); }

    public List<Booking> getBookings() { return bookings; }

    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public int getId() { return id; }

    public long getBalance() { return balance; }

    public String getPassword() { return password; }

    public void setBalance(long balance) throws SQLException, UnableToOpenDatabaseException, NotEnoughFundsException {
        if(balance < 0)
            throw new NotEnoughFundsException("You do not have enough money, please recharge your account.");
        this.balance = balance;
    }

    public void setId(int id) { this.id = id; }

}
