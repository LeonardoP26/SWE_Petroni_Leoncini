package dao.fake_daos;

import business_logic.exceptions.DatabaseFailedException;
import daos.CinemaDao;
import db.CinemaDatabaseTest;
import domain.Cinema;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FakeCinemaDao implements CinemaDao {

    @Override
    public void insert(@NotNull Cinema cinema) {

    }

    @Override
    public void update(@NotNull Cinema cinema, @NotNull Cinema copy) {
        cinema.copy(copy);
    }

    @Override
    public void delete(@NotNull Cinema cinema) {

    }

    @Override
    public List<Cinema> get() {
        return CinemaDatabaseTest.runQuery(
                "SELECT * FROM Cinemas",
                (res) -> {
                    ArrayList<Cinema> cinemas = new ArrayList<>();
                    while(res.next()){
                        Cinema c = new Cinema(res);
                        c.setName(res.getString("cinema_name"));
                        cinemas.add(c);
                    }
                    return cinemas;
                });
    }

    @Override
    public Cinema get(Cinema cinema) {
        return CinemaDatabaseTest.runQuery(
                "SELECT * FROM Cinemas WHERE cinema_id = %d".formatted(cinema.getId()),
                (res) -> {
                    if(!res.next())
                        return null;
                    Cinema c = new Cinema(res);
                    c.setName(res.getString("cinema_name"));
                    return c;
                });
    }
}
