package repository;

import models.Movie;

import java.sql.*;
import java.util.*;

public class MovieRepository {

    private final Connection conn;

    public MovieRepository(Connection conn) {
        this.conn = conn;
    }

    public List<Movie> findAll() {
        List<Movie> movies = new ArrayList<>();
        String sql = """
            SELECT id, title, director, description, release_year, genres, age_restriction, creator_id, created_at
            FROM media
            WHERE media_type = 'movie'
            """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                movies.add(mapResultSetToMovie(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public Optional<Movie> findById(UUID id) {
        String sql = """
            SELECT id, title, director, description, release_year, genres, age_restriction, creator_id, created_at
            FROM media
            WHERE media_type = 'movie' AND id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id); // ✅ UUID
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMovie(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void save(Movie movie) {
        try {
            if (movie.getId() == null) {
                // INSERT
                String sql = """
                INSERT INTO media (
                    title,
                    director,
                    description,
                    release_year,
                    genres,
                    age_restriction,
                    creator_id,
                    media_type
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, 'movie')
                RETURNING id
                """;

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, movie.getTitle());
                    stmt.setString(2, movie.getDirector());
                    stmt.setString(3, movie.getDescription());
                    stmt.setInt(4, movie.getReleaseYear());
                    stmt.setString(5, movie.getGenre());
                    stmt.setInt(6, movie.getAgeRestriction());
                    stmt.setObject(7, movie.getCreatorID());

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            movie.setId((UUID) rs.getObject("id"));
                        }
                    }
                }
            } else {
                // UPDATE
                String sql = """
                UPDATE media
                SET
                    title = ?,
                    director = ?,
                    description = ?,
                    release_year = ?,
                    genres = ?,
                    age_restriction = ?
                WHERE id = ? AND media_type = 'movie'
                """;

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, movie.getTitle());
                    stmt.setString(2, movie.getDirector());
                    stmt.setString(3, movie.getDescription());
                    stmt.setInt(4, movie.getReleaseYear());
                    stmt.setString(5, movie.getGenre());
                    stmt.setInt(6, movie.getAgeRestriction());
                    stmt.setObject(7, movie.getId());

                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean delete(UUID movieId, UUID currentUserId) {
        String sql = """
        DELETE FROM media
        WHERE id = ?
          AND creator_id = ?
          AND media_type = 'movie'
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            System.out.println("GEHT BEI REPO");
            stmt.setObject(1, movieId);
            stmt.setObject(2, currentUserId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private Movie mapResultSetToMovie(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setId((UUID) rs.getObject("id"));
        movie.setTitle(rs.getString("title"));
        movie.setDirector(rs.getString("director"));
        movie.setDescription(rs.getString("description"));
        movie.setReleaseYear(rs.getInt("release_year"));
        movie.setGenre(rs.getString("genres"));
        movie.setAgeRestriction(rs.getInt("age_restriction"));
        movie.setCreatorID((UUID) rs.getObject("creator_id"));
        movie.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return movie;
    }
}
