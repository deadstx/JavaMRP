package models;

import java.util.UUID;

public class Favorite {

    private UUID id;
    private UUID userId;
    private UUID mediaId;

    public Favorite(
            UUID id,
            UUID userId,
            UUID mediaId
    ) {
        this.id = id;
        this.userId = userId;
        this.mediaId = mediaId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getMediaId() {
        return mediaId;
    }

    public void setMediaId(UUID mediaId) {
        this.mediaId = mediaId;
    }

}
