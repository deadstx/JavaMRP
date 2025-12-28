package models;

import java.util.UUID;

public class Movie implements Media {

    private UUID id;
    private String title;
    private String director;
    private String description;
    private int release_year;
    private String genre;
    private int age_restriction;
    private UUID creator_id;
    private String created_at;
    private float rating;

    // ----- Konstruktoren -----

    public Movie() {
    }

    public Movie(UUID id, String title, String director, String description,
                 int release_year, String genre, int age_restriction, UUID creator_id) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.description = description;
        this.release_year = release_year;
        this.genre = genre;
        this.age_restriction = age_restriction;
        this.creator_id = creator_id;
    }

    // ----- Interface-Methoden -----

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getDirector() {
        return director;
    }

    @Override
    public void setDirector(String director) {
        this.director = director;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getReleaseYear() {
        return release_year;
    }

    @Override
    public void setReleaseYear(int release_year) {
        this.release_year = release_year;
    }

    @Override
    public String getGenre() {
        return genre;
    }

    @Override
    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public int getAgeRestriction() {
        return age_restriction;
    }

    @Override
    public void setAgeRestriction(int age_restriction) {
        this.age_restriction = age_restriction;
    }

    @Override
    public UUID getCreatorID() {
        return creator_id;
    }

    @Override
    public void setCreatorID(UUID creator_id) {
        this.creator_id = creator_id;
    }

    @Override
    public String getCreatedAt() {
        return created_at;
    }

    @Override
    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public float getRating() {
        return rating;
    }

    @Override
    public void setRating(float rating) {
        this.rating = rating;
    }
}
