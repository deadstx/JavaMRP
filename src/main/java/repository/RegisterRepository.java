package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterRepository {
    private final Connection conn;

    public RegisterRepository(Connection conn) {
        this.conn = conn;
    }


    public boolean userExists(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true, wenn ein Ergebnis existiert
            }
        }
    }


    public boolean createUser(String username, String hashedPassword) throws SQLException {

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();
        }
        return true;
    }
}
