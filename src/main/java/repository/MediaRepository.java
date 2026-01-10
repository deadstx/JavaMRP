package repository;

import dto.MediaWithRatingDto;
import exception.NotFoundException;
import models.Media;
import models.MediaFilter;
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
            SELECT id, title, description, director, release_year, genres,
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

    public List<Media> findByFilter(MediaFilter filterType, Object value) {
        List<Media> mediaList = new ArrayList<>();

        String baseSql = """
        SELECT m.id, m.title, m.description, m.director, m.release_year, m.genres,
               m.age_restriction, m.creator_id, m.created_at, m.media_type
        FROM media m
        """;

        String joinClause = "";
        String whereClause = "";
        String groupHavingClause = "";

        // Baue die SQL-Bedingungen basierend auf dem Filter
        switch (filterType) {
            case TITLE -> whereClause = "WHERE m.title ILIKE ?";
            case GENRE -> whereClause = "WHERE m.genres ILIKE ?";
            case RELEASE_YEAR -> whereClause = "WHERE m.release_year = ?";
            case AGE_RESTRICTION -> whereClause = "WHERE m.age_restriction = ?";
            case MIN_RATING -> {
                joinClause = "JOIN ratings r ON r.media_id = m.id";
                groupHavingClause = "GROUP BY m.id HAVING AVG(r.stars) >= ?";
            }
            default -> throw new NotFoundException();
        }

        String sql = String.join(" ",
                baseSql,
                joinClause,
                whereClause,
                groupHavingClause
        ).trim();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Setze den Parameter für PreparedStatement
            if (value instanceof String s) {
                stmt.setString(1, "%" + s + "%");
            } else if (value instanceof Integer i) {
                stmt.setInt(1, i);
            } else {
                throw new NotFoundException();
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



    public List<MediaWithRatingDto> findAllWithRating() {
        List<MediaWithRatingDto> result = new ArrayList<>();

        String sql = """
        SELECT
            m.id, m.title, m.description, m.director, m.release_year,
            m.genres, m.age_restriction, m.creator_id, m.created_at, m.media_type,
            COALESCE(AVG(r.stars), 0) AS avg_rating,
            COUNT(r.id) AS rating_count
        FROM media m
        LEFT JOIN ratings r ON r.media_id = m.id
        GROUP BY
            m.id, m.title, m.description, m.director,
            m.release_year, m.genres, m.age_restriction,
            m.creator_id, m.created_at, m.media_type
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Media media = mapResultSetToMedia(rs);

                MediaWithRatingDto dto = new MediaWithRatingDto();
                dto.setMedia(media);
                dto.setAverageRating(rs.getDouble("avg_rating"));
                dto.setRatingCount(rs.getInt("rating_count"));

                result.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


    public List<MediaWithRatingDto> findReccommendedMedias(String genre, int recommendationCount) {
        List<MediaWithRatingDto> result = new ArrayList<>();

        String sql = """
        SELECT
            m.id, m.title, m.description, m.director, m.release_year,
            m.genres, m.age_restriction, m.creator_id, m.created_at, m.media_type,
            COALESCE(AVG(r.stars), 0) AS avg_rating, /*OHNE RATING = 0 Sterne */
            COUNT(r.id) AS rating_count
        FROM media m
        LEFT JOIN ratings r ON r.media_id = m.id
        WHERE m.genres LIKE ?
        GROUP BY
            m.id, m.title, m.description, m.director,
            m.release_year, m.genres, m.age_restriction,
            m.creator_id, m.created_at, m.media_type
        ORDER BY avg_rating DESC
        LIMIT ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + genre + "%");
            stmt.setInt(2, recommendationCount);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Media media = mapResultSetToMedia(rs);

                    MediaWithRatingDto dto = new MediaWithRatingDto();
                    dto.setMedia(media);
                    dto.setAverageRating(rs.getDouble("avg_rating"));
                    dto.setRatingCount(rs.getInt("rating_count"));

                    result.add(dto);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }





    // ========================
// FIND BY ID MIT RATING
// ========================
    public MediaWithRatingDto findByIdWithRating(UUID id) {
        String sql = """
        SELECT
            m.id, m.title, m.description, m.director, m.release_year,
            m.genres, m.age_restriction, m.creator_id, m.created_at, m.media_type,
            COALESCE(AVG(r.stars), 0) AS avg_rating,
            COUNT(r.id) AS rating_count
        FROM media m
        LEFT JOIN ratings r ON r.media_id = m.id
        WHERE m.id = ?
        GROUP BY
            m.id, m.title, m.description, m.director,
            m.release_year, m.genres, m.age_restriction,
            m.creator_id, m.created_at, m.media_type
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Media media = mapResultSetToMedia(rs);

                    MediaWithRatingDto dto = new MediaWithRatingDto();
                    dto.setMedia(media);
                    dto.setAverageRating(rs.getDouble("avg_rating"));
                    dto.setRatingCount(rs.getInt("rating_count"));

                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }



    public List<Media> findByIdList(List<UUID> ids) {
        if (ids.isEmpty()) return List.of();

        String placeholders = ids.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        String sql = """
        SELECT id, title, description, director, release_year, genres,
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


    public boolean insert(Media media) {
        String sql = """
        INSERT INTO media (
            title,
            description,
            director,
            release_year,
            genres,
            age_restriction,
            creator_id,
            media_type
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING id
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getDescription());
            stmt.setString(3, media.getDirector());
            stmt.setInt(4, media.getReleaseYear());
            stmt.setString(5, media.getGenre());
            stmt.setInt(6, media.getAgeRestriction());
            stmt.setObject(7, media.getCreatorId());
            stmt.setString(8, media.getMediaType());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    media.setId((UUID) rs.getObject("id"));
                    return true;
                }
            }
        } catch (SQLException e) {
            return false;
        }

        return false;
    }


    public boolean update(Media media) {
        String sql = """
        UPDATE media
        SET
            description = ?,
            director = ?,
            release_year = ?,
            genres = ?,
            age_restriction = ?,
            media_type = ?
        WHERE id = ? AND creator_id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, media.getDescription());
            stmt.setString(2, media.getDirector());
            stmt.setInt(3, media.getReleaseYear());
            stmt.setString(4, media.getGenre());
            stmt.setInt(5, media.getAgeRestriction());
            stmt.setString(6, media.getMediaType());
            stmt.setObject(7, media.getId());
            stmt.setObject(8, media.getCreatorId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0; // true = Update erfolgreich
        } catch (SQLException e) {
            return false;
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

    public boolean existsByTitle(String title) {
        String sql = """
            SELECT 1 FROM media
            WHERE title = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsById(UUID mediaId) {
        String sql = """
            SELECT 1 FROM media
            WHERE id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, mediaId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // ========================
    // MAPPING
    // ========================
    private Media mapResultSetToMedia(ResultSet rs) throws SQLException {
        Media media = new Media();
        media.setId((UUID) rs.getObject("id"));
        media.setTitle(rs.getString("title"));
        media.setDescription(rs.getString("description"));
        media.setDirector(rs.getString("director"));
        media.setReleaseYear(rs.getInt("release_year"));
        media.setGenre(rs.getString("genres"));
        media.setAgeRestriction(rs.getInt("age_restriction"));
        media.setCreatorId((UUID) rs.getObject("creator_id"));
        media.setCreatedAt(rs.getString("created_at"));
        media.setMediaType((rs.getString("media_type")));
        return media;
    }
}
