package repository;

import data.DatabaseConfig;
import models.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovieRepository {

    private final Connection conn;

    // Konstruktor öffnet eine einzige Connection
    public MovieRepository() {
        try {
            this.conn = DriverManager.getConnection(
                    DatabaseConfig.URL,
                    DatabaseConfig.USER,
                    DatabaseConfig.PASSWORD
            );
            System.out.println("Database - MOVIE - Verbindung erfolgreich!");
        } catch (SQLException e) {
            throw new RuntimeException("DB-Verbindung fehlgeschlagen!", e);
        }
    }

    public List<Movie> findAll() {
        List<Movie> movies = new ArrayList<>();
        String sql = """
            SELECT m.id, m.title, m.release_year, m.creator_user_id,
                   g.name AS genre
            FROM media m
            LEFT JOIN media_genres mg ON m.id = mg.media_id
            LEFT JOIN genres g ON mg.genre_id = g.id
            WHERE m.media_type='MOVIE'
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

    public Optional<Movie> findById(int id) {
        String sql = """
            SELECT m.id, m.title, m.release_year, m.creator_user_id,
                   g.name AS genre
            FROM media m
            LEFT JOIN media_genres mg ON m.id = mg.media_id
            LEFT JOIN genres g ON mg.genre_id = g.id
            WHERE m.media_type='MOVIE' AND m.id=?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
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
            conn.setAutoCommit(false);

            if (movie.getId() == 0) {
                // INSERT
                String sql = """
                    INSERT INTO media (title, media_type, release_year, creator_user_id)
                    VALUES (?, 'MOVIE', ?, ?)
                    RETURNING id
                    """;
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, movie.getTitle());
                    stmt.setInt(2, movie.getYear());
                    // stmt.setInt(3, movie.getCreatorUserId()); // optional
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            movie.setId(rs.getInt(1));
                        }
                    }
                }
            } else {
                // UPDATE
                String sql = "UPDATE media SET title=?, release_year=? WHERE id=?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, movie.getTitle());
                    stmt.setInt(2, movie.getYear());
                    stmt.setInt(3, movie.getId());
                    stmt.executeUpdate();
                }

                // Genre löschen
                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM media_genres WHERE media_id=?")) {
                    stmt.setInt(1, movie.getId());
                    stmt.executeUpdate();
                }
            }

            // Genre setzen (nur ein Genre)
            if (movie.getGenre() != null) {
                int genreId = getOrCreateGenre(movie.getGenre());
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO media_genres (media_id, genre_id) VALUES (?, ?)")) {
                    stmt.setInt(1, movie.getId());
                    stmt.setInt(2, genreId);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM media WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Hilfsmethoden

    private Movie mapResultSetToMovie(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setId(rs.getInt("id"));
        movie.setTitle(rs.getString("title"));
        movie.setYear(rs.getInt("release_year"));
        movie.setGenre(rs.getString("genre")); // nur ein Genre
        return movie;
    }

    private int getOrCreateGenre(String genreName) throws SQLException {
        // prüfen, ob Genre existiert
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT id FROM genres WHERE name=?")) {
            stmt.setString(1, genreName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }

        // Genre erstellen
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO genres (name) VALUES (?) RETURNING id")) {
            stmt.setString(1, genreName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }

        throw new SQLException("Genre konnte nicht erstellt werden: " + genreName);
    }

    // Optional: Connection schließen, wenn Repository nicht mehr gebraucht wird
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
