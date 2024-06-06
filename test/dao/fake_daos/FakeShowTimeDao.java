package dao.fake_daos;

import daos.ShowTimeDao;
import db.CinemaDatabaseTest;
import domain.Cinema;
import domain.Movie;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FakeShowTimeDao implements ShowTimeDao {

    @Override
    public void insert(@NotNull ShowTime showTime) {
        CinemaDatabaseTest.runQuery(
                "SELECT MAX(showtime_id) + 1 AS showtime_id FROM ShowTimes",
                (res) -> {
                    if(res.next())
                        showTime.setId(res);
                    else throw new RuntimeException();
                    return null;
                }
        );
    }

    @Override
    public void update(@NotNull ShowTime showTime, @NotNull ShowTime copy) {

    }

    @Override
    public void delete(@NotNull ShowTime showTime) {

    }

    @Override
    public List<ShowTime> get(@NotNull Movie movie, @NotNull Cinema cinema) {
        if(movie == CinemaDatabaseTest.getTestMovie1())
            return List.of(CinemaDatabaseTest.getTestShowTime1());
        if(movie == CinemaDatabaseTest.getTestMovie2())
            return List.of(CinemaDatabaseTest.getTestShowTime2());
        return List.of();
    }

    @Override
    public ShowTime get(ShowTime showTime) {
        return showTime;
    }
}
