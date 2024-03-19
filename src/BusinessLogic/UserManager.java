package BusinessLogic;

import Domain.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    private Map<UUID, User> users = new HashMap<>();

    public User createUser(String username, boolean isVip) throws UserAlreadyExistsException {
        for(User user : users.values()) {
            if(user.getUsername().equals(username)) {
                throw new UserAlreadyExistsException();
            }
        }

        User newUser = new User(username, isVip, //TODO Passarlo da DB);
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    //Altre funzioni per la gestione degli utenti ...
}
