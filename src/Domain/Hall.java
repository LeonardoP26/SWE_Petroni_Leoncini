package Domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.lang.Math.abs;

public class Hall {

    public Hall(ArrayList<SeatsRow> seatsRows, ArrayList<Schedule> schedules){
        for (SeatsRow row: seatsRows){
            if(!this.seatsRows.containsKey(row.getRow()))
                this.seatsRows.put(row.getRow(), row);
        }
        this.schedules = schedules;
    }

    private Map<Character, SeatsRow> seatsRows = new HashMap<>();
    private ArrayList<Schedule> schedules;


    /* TODO Maybe it's better to place this fun in the business logic part. */
    public boolean searchSeat(Seat seat) throws InvalidSeatException {
        SeatsRow row = seatsRows.get(seat.getRow());
        if(row == null)
            throw new InvalidSeatException("Row does not exist.");
        Seat s = row.getSeats().get(seat.getNumber());
        if(s == null)
            throw new InvalidSeatException("There is no seat with this number.");
        return s.isBooked();
    }

    /* TODO Maybe it's better to place this fun in the business logic part. */
    public LocalDateTime canAddShow(Duration movieDuration){
        Duration d = movieDuration.plus(Duration.ofMinutes(30));
        for (int i = 0; i < schedules.size() - 1; i++){
            Schedule s1 = schedules.get(i);
            Schedule s2 = schedules.get(i + 1);
            long diff = abs(ChronoUnit.MINUTES.between(s1.getDate(), s2.getDate()) - s1.getMovie().getDuration().toMinutes());
            if(Duration.ofMinutes(diff).compareTo(d) > 0)
                return s1.getDate().plus(d);
        }
        return null;
    }


}
