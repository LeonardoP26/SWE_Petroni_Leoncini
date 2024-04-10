package Domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ThreeDHall extends Hall {


    private final int extra3d = 2;
    private final HallTypes type = HallTypes.THREE_D;

    public ThreeDHall(ResultSet res) throws SQLException {
        super(res);
    }

    public ThreeDHall(int hallNumber){
        super(hallNumber);
    }

    @Override
    public int getCost() {
        return cost + extra3d;
    }

    protected int getExtra3d() {
        return extra3d;
    }

    @Override
    public HallTypes getHallType() {
        return type;
    }

}
