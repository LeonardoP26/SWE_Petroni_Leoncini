package Domain;


public class User {

    public User(int id, String username, long balance){
        this.id = id;
        this.username = username;
        this.balance = balance;
    }

    private String username;
    private int id;
    private long balance;


    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public long getBalance() {
        return balance;
    }
}
