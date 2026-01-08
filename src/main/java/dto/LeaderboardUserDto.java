package dto;

import java.util.UUID;

public class LeaderboardUserDto {

    private UUID id;
    private String username;
    private int ratingCount;
    private int leaderboardPlacement;

    public LeaderboardUserDto(UUID id, int ratingCount) {
        this.id = id;
        this.ratingCount = ratingCount;
    }


    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public int getRatingCount() { return ratingCount; }
    public int getLeaderboardPlacement() {return leaderboardPlacement; }
    public void setUsername(String username) {this.username = username;}
    public void setLeaderboardPlacement(int leaderboardPlacement) {this.leaderboardPlacement = leaderboardPlacement;}

}
