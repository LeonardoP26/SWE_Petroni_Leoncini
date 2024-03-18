package Domain;

import java.util.UUID;

public class Movie {
    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public float getRating() {
        return rating;
    }

    public UUID getId() {
        return id;
    }

    private String name;
    private String genre;
    private int durationInSeconds;

    public void setRating(float rating) {
        this.rating = rating;
    }

    private float rating;
    private UUID id;

public Movie(String name, String genre, int durationInSeconds, float rating, UUID id) {
        this.name = name;
        this.genre = genre;
        this.durationInSeconds = durationInSeconds;
        this.rating = rating;
    this.id = id;
    }
}
