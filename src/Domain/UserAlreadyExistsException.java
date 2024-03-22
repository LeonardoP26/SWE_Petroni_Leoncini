package Domain;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException() {
        super("Un utente con questo nome esiste già nel sistema.");
    //TODO
    }
}