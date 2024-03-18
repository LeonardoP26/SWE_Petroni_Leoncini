package Domain;

import java.util.List;
import java.util.UUID;

public class Hall {
    private UUID id;
    private List<SeatsRow> seatsRows;
    private List<Schedule> schedules;

    public List<SeatsRow> getSeatsRows() {
        return seatsRows;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public Hall(List<SeatsRow> seatsRows, List<Schedule> schedules, UUID id) {
        this.seatsRows = seatsRows;
        this.schedules = schedules;
        this.id = id;
    }

}
