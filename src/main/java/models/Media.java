package models;

import java.util.UUID;

public class Media {

    private UUID id;
    private String title;
    private String director;
    private String description;
    private String media_type;
    private int release_year;
    private String genre;
    private int age_restriction;
    private UUID creator_id;
    private String created_at;

    public Media() {}

    public Media(UUID id, String title, String director, String description, String media_type,
                 int release_year, String genre, int age_restriction, UUID creator_id) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.description = description;
        this.media_type = media_type;
        this.release_year = release_year;
        this.genre = genre;
        this.age_restriction = age_restriction;
        this.creator_id = creator_id;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMediaType() {
        return media_type;
    }

    public void setMediaType(String media_type) {
        this.media_type = media_type;
    }

    public int getReleaseYear() {
        return release_year;
    }

    public void setReleaseYear(int release_year) {
        this.release_year = release_year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getAgeRestriction() {
        return age_restriction;
    }

    public void setAgeRestriction(int age_restriction) {
        this.age_restriction = age_restriction;
    }

    public UUID getCreatorId() {
        return creator_id;
    }

    public void setCreatorId(UUID creator_id) {
        this.creator_id = creator_id;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

}
