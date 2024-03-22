package Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cinema {

    private Integer id = null;

    public Cinema() {}


    public Cinema(int id){
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
