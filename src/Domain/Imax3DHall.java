package Domain;

public class Imax3DHall extends Hall {

    private final ThreeDHall hall3d;
    private final ImaxHall imaxHall;
    private final HallTypes type = HallTypes.IMAX_3D;

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
