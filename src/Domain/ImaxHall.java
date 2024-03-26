package Domain;

public class ImaxHall extends Hall {

    private final int imaxExtra = 4;
    private final HallTypes type = HallTypes.IMAX;

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
