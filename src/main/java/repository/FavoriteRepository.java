package repository;

import models.Favorite;


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

    private void addNewFavorite(Favorite favorite) throws SQLException {
        String sql = """
            INSERT INTO media (
                media_id,
                user_id
            )
            VALUES (?, ?)
            RETURNING id
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, favorite.getMediaId());
            stmt.setObject(2, favorite.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    favorite.setId((UUID) rs.getObject("id"));
                }
            }
        }
    }

    public boolean removeFromFavorites(UUID mediaId, UUID currentUserId) {
        String sql = """
            DELETE FROM favorites
            WHERE media_id = ?
              AND creator_id = ?
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

    public List<UUID> findFavoritesByUser(UUID currentUserId) {
        List<UUID> mediaIds = new ArrayList<>();

        String sql = """
            SELECT media_id
            FROM favorites
            WHERE user_id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, currentUserId);

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
