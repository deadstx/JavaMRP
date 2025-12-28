package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
