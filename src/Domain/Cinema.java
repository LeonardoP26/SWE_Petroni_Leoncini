package Domain;

import java.util.List;
import java.util.UUID;

public class Cinema {
    public List<Hall> getHalls() {
        return halls;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    private List<Hall> halls;
    private String name;
    private UUID id;

    public Cinema(List<Hall> halls, String name, UUID id) {
        this.halls = halls;
        this.name = name;
        this.id = id;
    }
}
