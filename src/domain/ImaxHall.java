package domain;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ImaxHall extends Hall {

    private final int imaxExtra = 4;
    private final HallTypes type = HallTypes.IMAX;

    public ImaxHall(ResultSet res) throws SQLException {
        super(res);
    }

    public ImaxHall(@NotNull ImaxHall hall){
        super(hall);
    }

    public ImaxHall(int hallNumber, Cinema cinema){
        super(hallNumber, cinema);
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
