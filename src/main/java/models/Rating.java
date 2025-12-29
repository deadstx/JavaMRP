package models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Rating {

    private final UUID id;
    private final UUID userId;
    private final UUID mediaId;
    private final int stars;
    private final String comment;
    private final String createdAt;

    public Rating(
            UUID id,
            UUID userId,
            UUID mediaId,
            int stars,
            String comment,
            String createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.mediaId = mediaId;
        this.stars = stars;
        this.comment = comment;
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

    public String getComment() {
        return comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

}
