package models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Rating {

    private UUID id;
    private UUID userId;
    private UUID mediaId;
    private int stars;
    private String createdAt;

    public Rating() {}

    public Rating(
            UUID id,
            UUID userId,
            UUID mediaId,
            int stars,
            String createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.mediaId = mediaId;
        this.stars = stars;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getMediaId() {
        return mediaId;
    }

    public int getStars() {
        return stars;
    }


    public String getCreatedAt() {
        return createdAt;
    }


    public void setId(UUID id) {
        this.id = id;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setMediaId(UUID mediaId) {
        this.mediaId = mediaId;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // helper für POST

    public void initNewRating(UUID userId, UUID mediaId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.mediaId = mediaId;
        this.createdAt = LocalDateTime.now().toString();
    }
}
