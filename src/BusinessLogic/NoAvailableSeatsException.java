package BusinessLogic;

public class NoAvailableSeatsException extends Exception {
    public NoAvailableSeatsException() {
        super("Non ci sono posti disponibili per la prenotazione richiesta.");
        //TODO
    }
}
