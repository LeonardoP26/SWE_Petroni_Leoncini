package Domain;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ImaxHall extends Hall {

    private final int imaxExtra = 4;
    private final HallTypes type = HallTypes.IMAX;

    public ImaxHall(ResultSet res) throws SQLException {
        this(res.getInt(1), res.getInt(2));
    }

    public ImaxHall(int id, int cinemaId) {
        super(id, cinemaId);
    }

    @Override
    public int getCost(){
        return cost + imaxExtra;
    }

    public int getImaxExtra() {
        return imaxExtra;
    }

    @Override
    public HallTypes getHallType(){
        return type;
    }

}
