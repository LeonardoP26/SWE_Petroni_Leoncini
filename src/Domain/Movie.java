package Domain;

import java.util.UUID;

public class Movie {
    public String getName() {
        return name;
    }
    public String getGenre() {
        return genre;
    }
    public int getDurationInMinutes() {
        return durationInMinutes;
    }
    public float getRating() {
        return rating;
    }
    public UUID getId() {
        return id;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }


    private String title;
    private String genre;
    private int durationInMinutes;
    private float rating;
    private UUID id;

public Movie(String title, String genre, int durationInMinutes, float rating, UUID id) {
        this.title = title;
        this.genre = genre;
        this.durationInMinutes = durationInMinutes;
        this.rating = rating;
    this.id = id;
    }

    // Override di equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return id == movie.id &&
                Float.compare(movie.rating, rating) == 0 &&
                Objects.equals(title, movie.title) &&
                Objects.equals(genre, movie.genre) &&
                Objects.equals(durationInMinutes, movie.durationInMinutes);
    }
}
