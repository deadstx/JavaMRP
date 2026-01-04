package repository;

import models.Rating;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RatingRepository {

    private final Connection conn;

    public RatingRepository(Connection conn) {
        this.conn = conn;
    }

    /* ---------------------------------------------------
     * CREATE
     * --------------------------------------------------- */
    public boolean addNewRating(Rating rating) {
        String sql = """
            INSERT INTO ratings (id, user_id, media_id, stars, comment)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, rating.getId());
            stmt.setObject(2, rating.getUserId());
            stmt.setObject(3, rating.getMediaId());
            stmt.setInt(4, rating.getStars());
            stmt.setString(5, rating.getComment());


            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ---------------------------------------------------
     * READ
     * --------------------------------------------------- */

    public Rating findById(UUID ratingId) {
        String sql = "SELECT * FROM ratings WHERE id = ?";
        System.out.println("Rating ID im Repo: " + ratingId);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, ratingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return fromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    // ALLE RATINGS EINES USERS
    public List<Rating> findByUserId(UUID userId) {
        String sql = """
            SELECT *
            FROM ratings
            WHERE user_id = ?
            ORDER BY created_at DESC
            """;

        List<Rating> ratings = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ratings.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ratings;
    }

    // ALLE RATINGS ZU EINEM MEDIA EINTRAG
    public List<Rating> findByMediaId(UUID mediaId) {
        String sql = """
            SELECT *
            FROM ratings
            WHERE media_id = ?
            ORDER BY created_at DESC
            """;

        List<Rating> ratings = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, mediaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ratings.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ratings;
    }


    public boolean existsByUserAndMedia(UUID userId, UUID mediaId) {
        String sql = """
            SELECT 1
            FROM ratings
            WHERE user_id = ? AND media_id = ?
            LIMIT 1
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            stmt.setObject(2, mediaId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ---------------------------------------------------
     * DELETE
     * --------------------------------------------------- */
    public boolean deleteById(UUID mediaId, UUID currentUserId) {
        String sql = "DELETE FROM ratings WHERE media_id = ? AND user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, mediaId);
            stmt.setObject(2, currentUserId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ---------------------------------------------------
     * UPDATE STATUS
     * --------------------------------------------------- */
    public boolean updateRatingStatus(UUID ratingId, UUID currentUserId) {
        String sql = """
        UPDATE ratings
        SET is_confirmed = TRUE
        WHERE id = ? AND user_id = ?
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, ratingId);
            stmt.setObject(2, currentUserId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    /* ---------------------------------------------------
     * MAPPER
     * --------------------------------------------------- */

    private Rating fromResultSet(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");

        return new Rating(
                rs.getObject("id", UUID.class),
                rs.getObject("user_id", UUID.class),
                rs.getObject("media_id", UUID.class),
                rs.getInt("stars"),
                rs.getString("comment"),
                rs.getString("created_at")
        );
    }
}
