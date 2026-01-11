package repository;

import exception.ServerErrorException;
import models.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommentRepository {

    private final Connection conn;

    public CommentRepository(Connection conn) {
        this.conn = conn;
    }

    /* ---------------------------------------------------
     * CREATE
     * --------------------------------------------------- */
    public boolean addNewComment(UUID mediaId, UUID currentUserId, String commentText) {
        String sql = """
            INSERT INTO comments (comment_text, user_id, media_id)
            VALUES (?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, commentText);
            stmt.setObject(2, currentUserId);
            stmt.setObject(3, mediaId);

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

    // ALLE KOMMENTARE ZU EINEM MEDIA EINTRAG
    public List<Comment> findAllCommentsByMedia(UUID mediaId) {
        String sql = """
            SELECT *
            FROM comments
            WHERE media_id = ? AND is_confirmed = true
            ORDER BY created_at DESC
            """;

        List<Comment> comments = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, mediaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                comments.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comments;
    }

    // ALLE KOMMENTARE EINES USERS
    public List<Comment> findAllCommentsByUser(UUID currentUserId) {
        String sql = """
            SELECT *
            FROM comments
            WHERE user_id = ?
            ORDER BY created_at DESC
            """;

        List<Comment> comments = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, currentUserId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                comments.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comments;
    }

    /* ---------------------------------------------------
     * DELETE
     * --------------------------------------------------- */
    public boolean deleteComment(UUID mediaId, UUID currentUserId) {
        String sql = """
            DELETE FROM comments
            WHERE media_id = ? AND user_id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, mediaId);
            stmt.setObject(2, currentUserId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ---------------------------------------------------
     * UPDATE STATUS
     * --------------------------------------------------- */
    public boolean confirmComment(UUID mediaId, UUID currentUserId) {
        String sql = """
            UPDATE comments
            SET is_confirmed = TRUE
            WHERE media_id = ? AND user_id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, mediaId);
            stmt.setObject(2, currentUserId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean commentExistsByUserAndMedia(UUID mediaId, UUID currentUserId) {
        String sql = """
            SELECT 1
            FROM comments
            WHERE media_id = ? AND user_id = ?
            LIMIT 1
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, mediaId);
            stmt.setObject(2, currentUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new ServerErrorException();
        }
    }


    /* ---------------------------------------------------
     * MAPPER
     * --------------------------------------------------- */
    private Comment fromResultSet(ResultSet rs) throws SQLException {
        return new Comment(
                rs.getObject("id", UUID.class),
                rs.getObject("user_id", UUID.class),
                rs.getObject("media_id", UUID.class),
                rs.getString("comment_text")
        );
    }
}
