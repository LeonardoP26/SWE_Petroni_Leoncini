package BusinessLogic;

import Domain.*;

public class HallFactory {

    public static Hall crateHall(int id, int cinemaId, String type) throws IllegalArgumentException{
        return switch (Hall.HallTypes.valueOf(type)){
            case STANDARD -> new Hall(id, cinemaId);
            case IMAX -> new ImaxHall(id, cinemaId);
            case THREE_D -> new ThreeDHall(id, cinemaId);
            case IMAX_3D -> new Imax3DHall(id, cinemaId);
        };
    }

}
