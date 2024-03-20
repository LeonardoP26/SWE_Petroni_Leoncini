package Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cinema {

    private ArrayList<Hall> halls = new ArrayList<>();


    public Cinema(){

    }




    public ArrayList<Hall> getHalls() {
        return halls;
    }

    public void setHalls(ArrayList<Hall> halls) {
        this.halls = halls;
    }


}
