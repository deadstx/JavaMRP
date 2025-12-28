package models;
import java.util.UUID;

public interface Media {
    UUID getId();
    void setId(UUID id);

    String getTitle();
    void setTitle(String title);

    String getDirector();
    void setDirector(String director);

    String getDescription();
    void setDescription(String description);

    int getReleaseYear();
    void setReleaseYear(int release_year);

    String getGenre();
    void setGenre(String genre);

    int getAgeRestriction();
    void setAgeRestriction(int age_restriction);

    UUID getCreatorID();
    void setCreatorID(UUID creator_id);

    String getCreatedAt();
    void setCreatedAt(String created_at);

    float getRating();
    void setRating(float rating);
}
