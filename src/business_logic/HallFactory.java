package business_logic;

import domain.Hall;
import domain.Imax3DHall;
import domain.ImaxHall;
import domain.ThreeDHall;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HallFactory {

    public static @NotNull Hall createHall(@NotNull ResultSet res) throws SQLException {
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

    public static @NotNull Hall createHall(@NotNull Hall hall){
        return switch (hall) {
            case ImaxHall imaxHall -> new ImaxHall(imaxHall);
            case ThreeDHall threeDHall -> new ThreeDHall(threeDHall);
            case Imax3DHall imax3DHall -> new Imax3DHall(imax3DHall);
            default -> new Hall(hall);
        };
    }

}
