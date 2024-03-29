package Domain;

import BusinessLogic.UnableToOpenDatabaseException;
import BusinessLogic.repositories.BookingRepository;
import BusinessLogic.repositories.BookingRepositoryInterface;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Booking implements DatabaseEntity{

    public Booking(ResultSet res) throws SQLException {
        Set<Integer> seatsId = new HashSet<>();
        Set<Integer> usersId = new HashSet<>();
        while(res.next()){
            seatsId.add(res.getInt(2));
            usersId.add(res.getInt(3));
        }
        this.bookingNumber = res.getInt(4);
        this.showTimeId = res.getInt(1);
        this.seatsId = seatsId.stream().toList();
        this.usersId = usersId.stream().toList();
    }

    public Booking(int bookingNumber, int showTimeId, List<Integer> seatsId, List<Integer> usersId) {
        this.bookingNumber = bookingNumber;
        this.showTimeId = showTimeId;
        this.seatsId = seatsId;
        this.usersId = usersId;
    }

    private final int bookingNumber;
    private final int showTimeId;
    private final List<Integer> seatsId;
    private final List<Integer> usersId;

    private final BookingRepositoryInterface bookRepo = BookingRepository.getInstance();


    @Override
    public int getId() {
        return bookingNumber;
    }

    public int getShowTimeId() {
        return showTimeId;
    }

    public List<Integer> getSeatsId() {
        return seatsId;
    }

    public List<Integer> getUsersId() {
        return usersId;
    }




    public List<User> getBookingUsers() throws SQLException, UnableToOpenDatabaseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return bookRepo.getBookingUsers(this);
    }

    public List<Seat> getBookingSeats() throws SQLException, UnableToOpenDatabaseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return bookRepo.getBookingSeats(this);
    }

    public ShowTime getBookingShowTime() throws SQLException, UnableToOpenDatabaseException{
        return bookRepo.getBookingShowTime(this);
    }




}
