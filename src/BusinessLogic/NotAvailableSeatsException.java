package BusinessLogic;

import Domain.Seat;

import java.util.List;

public class NotAvailableSeatsException extends Exception{

    public NotAvailableSeatsException(String message){
        super(message);
    }

}
