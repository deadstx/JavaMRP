package repository;

import exception.ServerErrorException;
import models.Favorite;

import java.rmi.ServerError;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FavoriteRepository {

    private final Connection conn;

    public FavoriteRepository(Connection conn) {
        this.conn = conn;
    }

    public boolean checkIfExists(UUID mediaId, UUID userId) {
        String sql = """
            SELECT 1
            FROM favorites
            WHERE user_id = ? AND media_id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            stmt.setObject(2, mediaId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true = existiert, false = existiert nicht
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean markAsFavorite(UUID mediaId, UUID userId) {
        String sql = """
        INSERT INTO favorites (media_id, user_id)
        VALUES (?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, mediaId);
            stmt.setObject(2, userId);

            return stmt.executeUpdate() == 1; // true = erfolgreich eingefügt
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean removeFromFavorites(UUID mediaId, UUID currentUserId) {
        String sql = """
            DELETE FROM favorites
            WHERE media_id = ?
              AND user_id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, mediaId);
            stmt.setObject(2, currentUserId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getFavoriteGenre(UUID userId) {
        String sql = """
        SELECT genres
        FROM media
        WHERE creator_id = ?
        GROUP BY genres
        ORDER BY COUNT(*) DESC
        LIMIT 1
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("genres");
                }
            }
        } catch (SQLException e) {
            throw new ServerErrorException();
        }

        return null; // User hat keine Ratings
    }

    public List<UUID> findFavoritesByUser(UUID userId) {
        List<UUID> mediaIds = new ArrayList<>();

        String sql = """
            SELECT media_id
            FROM favorites
            WHERE user_id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mediaIds.add(rs.getObject("media_id", UUID.class));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mediaIds;
    }


    private Favorite mapResultSetToFavorite(ResultSet rs) throws SQLException {
        return new Favorite(
                rs.getObject("id", UUID.class),
                rs.getObject("media_id", UUID.class),
                rs.getObject("user_id", UUID.class)
        );
    }
}
