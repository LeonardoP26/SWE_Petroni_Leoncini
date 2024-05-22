package dao.fake_daos;

import business_logic.HallFactory;
import business_logic.exceptions.DatabaseFailedException;
import daos.HallDao;
import db.CinemaDatabaseTest;
import domain.Cinema;
import domain.Hall;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

public class FakeHallDao implements HallDao {

    @Override
    public void insert(@NotNull Hall hall) {
        CinemaDatabaseTest.runQuery(
                "SELECT MAX(hall_id) + 1 AS hall_id FROM Halls",
                (res) -> {
                    if (res.next())
                        hall.setId(res);
                    return hall;
                });
    }

    @Override
    public void update(@NotNull Hall hall, @NotNull Hall copy) {

    }

    @Override
    public void delete(@NotNull Hall hall) {

    }

    @Override
    public Hall get(@NotNull ShowTime showTime) {
        return CinemaDatabaseTest.runQuery(
                "SELECT * FROM ShowTimes JOIN Halls ON ShowTimes.hall_id = Halls.hall_id WHERE showtime_id = %d"
                        .formatted(showTime.getId()),
                (res) -> {
                    if(!res.next())
                        return null;
                    Hall h = HallFactory.createHall(res);
                    h.setHallNumber(res.getInt("hall_number"));
                    return h;
                });
    }

    @Override
    public Hall get(Hall hall) {
        return CinemaDatabaseTest.runQuery(
                "SELECT * FROM halls WHERE hall_id = %d".formatted(hall.getId()),
                (res) -> {
                    if (!res.next())
                        return null;
                    Hall h = HallFactory.createHall(res);
                    h.setHallNumber(res.getInt("hall_number"));
                    return h;
                });
    }
}
