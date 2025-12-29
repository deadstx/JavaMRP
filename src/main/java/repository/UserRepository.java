package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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
}
