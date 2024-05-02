package business_logic;

import domain.Booking;
import org.jetbrains.annotations.NotNull;

public class ReceiptPrinter implements Observer<Booking> {

    private static ReceiptPrinter instance = null;

    public static ReceiptPrinter getInstance() {
        if(instance == null)
            instance = new ReceiptPrinter();
        return instance;
    }

    private ReceiptPrinter(){}


    @Override
    public void update(@NotNull Booking entity) {
        System.out.println("Order #" + entity.getBookingNumber() + ". You have booked for " + entity.getSeats().size() + " - " + entity.getShowTime().getName());
    }
}
