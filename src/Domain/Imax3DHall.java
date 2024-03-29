package Domain;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Imax3DHall extends Hall {

    private final ThreeDHall hall3d;
    private final ImaxHall imaxHall;
    private final HallTypes type = HallTypes.IMAX_3D;

    public Imax3DHall(ResultSet res) throws SQLException {
        this(res.getInt(1), res.getInt(2));
    }

    public Imax3DHall(int id, int cinemaId) {
        super(id, cinemaId);
        hall3d = new ThreeDHall(id, cinemaId);
        imaxHall = new ImaxHall(id, cinemaId);
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
