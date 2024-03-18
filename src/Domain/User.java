package Domain;

import java.util.UUID;

public class User {

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    private String username;
    private boolean isVip;
    private UUID id;

    public User(String username, boolean isVip, UUID id) {
        this.username = username;
        this.isVip = isVip;
        this.id = id;
    }


}
