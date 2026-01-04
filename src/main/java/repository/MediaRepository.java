package repository;

import models.Media;
import models.MediaType;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class MediaRepository {

    private final Connection conn;

    public MediaRepository(Connection conn) {
        this.conn = conn;
    }

    // ========================
    // FIND ALL (optional nach Typ)
    // ========================
    public List<Media> findAll(Optional<MediaType> mediaType) {
        List<Media> mediaList = new ArrayList<>();

        String sql = """
            SELECT id, title, description, release_year, genres,
                   age_restriction, creator_id, created_at, media_type
            FROM media
            """ + (mediaType.isPresent() ? "WHERE media_type = ?" : "");

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (mediaType.isPresent()) {
                stmt.setString(1, mediaType.get().name());
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mediaList.add(mapResultSetToMedia(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mediaList;
    }

    // ========================
    // FIND BY ID
    // ========================
    public Optional<Media> findById(UUID id) {
        String sql = """
            SELECT id, title, description, release_year, genres,
                   age_restriction, creator_id, created_at, media_type
            FROM media
            WHERE id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMedia(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public List<Media> findByIdList(List<UUID> ids) {
        if (ids.isEmpty()) return List.of();

        String placeholders = ids.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        String sql = """
        SELECT id, title, description, release_year, genres,
               age_restriction, creator_id, created_at, media_type
        FROM media
        WHERE id IN (""" + placeholders + ")";

        List<Media> mediaList = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < ids.size(); i++) {
                stmt.setObject(i + 1, ids.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mediaList.add(mapResultSetToMedia(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mediaList;
    }



    // ========================
    // SAVE (INSERT / UPDATE)
    // ========================
    public void save(Media media) {
        try {
            if (media.getId() == null) {
                insert(media);
            } else {
                update(media);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insert(Media media) throws SQLException {
        String sql = """
            INSERT INTO media (
                title,
                description,
                release_year,
                genres,
                age_restriction,
                creator_id,
                media_type
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getDescription());
            stmt.setInt(3, media.getReleaseYear());
            stmt.setString(4, media.getGenre());
            stmt.setInt(5, media.getAgeRestriction());
            stmt.setObject(6, media.getCreatorId());
            stmt.setString(7, media.getMediaType());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    media.setId((UUID) rs.getObject("id"));
                }
            }
        }
    }

    private void update(Media media) throws SQLException {
        String sql = """
            UPDATE media
            SET
                title = ?,
                description = ?,
                release_year = ?,
                genres = ?,
                age_restriction = ?,
                media_type = ?
            WHERE id = ? AND creator_id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getDescription());
            stmt.setInt(3, media.getReleaseYear());
            stmt.setString(4, media.getGenre());
            stmt.setInt(5, media.getAgeRestriction());
            stmt.setString(6, media.getMediaType());
            stmt.setObject(7, media.getId());
            stmt.setObject(8, media.getCreatorId());

            stmt.executeUpdate();
        }
    }

    // ========================
    // DELETE (nur Ersteller)
    // ========================
    public boolean delete(UUID mediaId, UUID currentUserId) {
        String sql = """
            DELETE FROM media
            WHERE id = ?
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

    // ========================
    // MAPPING
    // ========================
    private Media mapResultSetToMedia(ResultSet rs) throws SQLException {
        Media media = new Media();
        media.setId((UUID) rs.getObject("id"));
        media.setTitle(rs.getString("title"));
        media.setDescription(rs.getString("description"));
        media.setReleaseYear(rs.getInt("release_year"));
        media.setGenre(rs.getString("genres"));
        media.setAgeRestriction(rs.getInt("age_restriction"));
        media.setCreatorId((UUID) rs.getObject("creator_id"));
        media.setCreatedAt(rs.getString("created_at"));
        media.setMediaType((rs.getString("media_type")));
        return media;
    }
}
