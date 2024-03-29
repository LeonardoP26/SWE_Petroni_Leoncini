package Domain;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ThreeDHall extends Hall {


    private final int extra3d = 2;
    private final HallTypes type = HallTypes.THREE_D;

    public ThreeDHall(ResultSet res) throws SQLException {
        this(res.getInt(1), res.getInt(2));
    }

    public ThreeDHall(int id, int cinemaId) {
        super(id, cinemaId);
    }

    @Override
    public int getCost() {
        return cost + extra3d;
    }

    public int getExtra3d() {
        return extra3d;
    }

    @Override
    public HallTypes getHallType() {
        return type;
    }

}
