package repository;

import models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ProfileRepository {

    private final Connection conn;

    public ProfileRepository(Connection conn) {
        this.conn = conn;
    }

    public User fetchUserProfile(UUID currentUserId) {
        String sql = "SELECT id, username FROM users WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, currentUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserProfile(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // falls kein User gefunden wurde
    }

    private User mapResultSetToUserProfile(ResultSet rs) throws SQLException {
        return new User(
                rs.getObject("id", UUID.class),
                rs.getString("username")
        );
    }

}
