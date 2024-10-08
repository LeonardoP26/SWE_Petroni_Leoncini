package domain;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Imax3DHall extends Hall {

    private final ThreeDHall hall3d;
    private final ImaxHall imaxHall;
    private final HallTypes type = HallTypes.IMAX_3D;

    public Imax3DHall(ResultSet res) throws SQLException {
        super(res);
        hall3d = new ThreeDHall(res);
        imaxHall = new ImaxHall(res);
    }

    public Imax3DHall (int hallNumber, Cinema cinema){
        super(hallNumber, cinema);
        hall3d = new ThreeDHall(hallNumber, cinema);
        imaxHall = new ImaxHall(hallNumber, cinema);
    }

    public Imax3DHall(@NotNull Imax3DHall hall){
        super(hall);
        hall3d = new ThreeDHall(hall.getHallNumber(), hall.getCinema());
        imaxHall = new ImaxHall(hall.getHallNumber(), hall.getCinema());
    }

    @Override
    public int getCost() {
        return cost + imaxHall.getImaxExtra() + hall3d.getExtra3d();
    }

    @Override
    public HallTypes getHallType(){
        return type;
    }


}
