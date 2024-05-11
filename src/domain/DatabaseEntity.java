package domain;

// Marker Interface
public interface DatabaseEntity {

    int ENTITY_WITHOUT_ID = -1;

    String getName();

    int getId();

    void resetId();
}
