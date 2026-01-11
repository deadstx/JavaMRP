package dto;

import java.util.UUID;

public class UserProfileDto {

    private UUID id;
    private String username;
    private int ratingCount;
    private String favoriteGenre;

    public UserProfileDto(UUID id, String username, int ratingCount, String favoriteGenre) {
        this.id = id;
        this.username = username;
        this.ratingCount = ratingCount;
        this.favoriteGenre = favoriteGenre;
    }

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public int getRatingCount() { return ratingCount; }
    public String getFavoriteGenre() {return favoriteGenre; }
}
