package Domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ImaxHall extends Hall {

    private final int imaxExtra = 4;
    private final HallTypes type = HallTypes.IMAX;

    public ImaxHall(ResultSet res) throws SQLException {
        super(res);
    }

    public ImaxHall(int hallNumber){
        super(hallNumber);
    }

    @Override
    public int getCost(){
        return cost + imaxExtra;
    }

    protected int getImaxExtra() {
        return imaxExtra;
    }

    @Override
    public HallTypes getHallType(){
        return type;
    }

}
