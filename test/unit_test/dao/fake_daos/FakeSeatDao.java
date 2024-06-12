package unit_test.dao.fake_daos;

import daos.SeatDao;
import db.CinemaDatabaseTest;
import domain.Hall;
import domain.Seat;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FakeSeatDao implements SeatDao {

    @Override
    public void insert(@NotNull Seat seat, @NotNull Hall hall) {
        CinemaDatabaseTest.runQuery(
                "SELECT MAX(seat_id) + 1 AS seat_id FROM seats",
                (res) -> {
                    if(res.next())
                        seat.setId(res);
                    return null;
                }
        );
    }

    @Override
    public void update(@NotNull Seat seat, @NotNull Seat copy, @NotNull Hall hall) {

    }

    @Override
    public void delete(@NotNull Seat seat) {

    }

    @Override
    public List<Seat> get(@NotNull ShowTime showTime) {
        return CinemaDatabaseTest.runQuery(
                "SELECT * FROM Seats JOIN ShowTimes ON Seats.hall_id = ShowTimes.hall_id WHERE showtime_id = %d".formatted(showTime.getId()),
                (res) -> {
                    ArrayList<Seat> seats = new ArrayList<>();
                    while (res.next()) {
                        Seat seat = new Seat(res);
                        seat.setRow(res.getString("row").charAt(0));
                        seat.setNumber(res.getInt("number"));
                        seats.add(seat);
                    }
                    return seats;
                }
        );
    }

    @Override
    public Seat get(Seat seat) {
        return CinemaDatabaseTest.runQuery(
                "SELECT * FROM Seats WHERE seat_id = %d".formatted(seat.getId()),
                (res) -> {
                    if(!res.next())
                        return null;
                    Seat s = new Seat(res);
                    s.setRow(res.getString("row").charAt(0));
                    s.setNumber(res.getInt("number"));
                    return s;
                }
        );
    }
}
