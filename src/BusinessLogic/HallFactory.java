package BusinessLogic;

import Domain.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HallFactory {

    public static Hall crateHall(ResultSet res) throws IllegalArgumentException, SQLException {
        return switch (Hall.HallTypes.valueOf(res.getString(3))){
            case STANDARD -> new Hall(res);
            case IMAX -> new ImaxHall(res);
            case THREE_D -> new ThreeDHall(res);
            case IMAX_3D -> new Imax3DHall(res);
        };
    }

}
