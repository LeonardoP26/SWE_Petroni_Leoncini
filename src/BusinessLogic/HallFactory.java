package BusinessLogic;

import Domain.*;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HallFactory {

    public static @NotNull Hall crateHall(@NotNull ResultSet res) throws SQLException {
        String type;
        try{
            type = res.getString("Halls.type");
        } catch (SQLException e){
            type = res.getString("type");
        }
        return switch(Hall.HallTypes.valueOf(type)){
            case STANDARD -> new Hall(res);
            case IMAX -> new ImaxHall(res);
            case THREE_D -> new ThreeDHall(res);
            case IMAX_3D -> new Imax3DHall(res);
        };
    }

}
