package repository;

import dto.LeaderboardUserDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

public class UserRepository {
    private final Connection conn;

    public UserRepository(Connection conn) {
        this.conn = conn;
    }


    public String findPasswordHashByUsername(String username) {
        String sql = "SELECT password FROM users WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Fehler bei DB Zugriff", e);
        }
    }


    public UUID findIdByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // UUID direkt zurückgeben
                    return (UUID) rs.getObject("id");
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Fehler bei DB Zugriff", e);
        }
    }

    public List<LeaderboardUserDto> enrichLeaderboardUsers(List<LeaderboardUserDto> topUsers) {
        if (topUsers == null || topUsers.isEmpty()) {
            return new ArrayList<>();
        }

        String sql = "SELECT id, username FROM users WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int placement = 1;

            for (LeaderboardUserDto userDto : topUsers) {
                stmt.setObject(1, userDto.getId());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String username = rs.getString("username");
                        userDto.setUsername(username);   // Username setzen
                        userDto.setLeaderboardPlacement(placement); // Platzierung setzen
                        placement++;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topUsers;
    }






}
