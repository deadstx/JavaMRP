package models;

import java.util.UUID;

public class Comment {

    private UUID id;
    private UUID userId;
    private UUID mediaId;
    private String comment_text;

    public Comment(
            UUID id,
            UUID userId,
            UUID mediaId,
            String comment_text
    ) {
        this.id = id;
        this.userId = userId;
        this.mediaId = mediaId;
        this.comment_text = comment_text;
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

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

}
