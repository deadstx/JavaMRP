package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Movie;

import java.io.*;
import java.util.*;

public class JsonMovieRepository {

    private final ObjectMapper mapper = new ObjectMapper();
    private List<Movie> movies;

    public JsonMovieRepository() {
        this.movies = loadMovies();
    }

    // Lädt Filme beim Start
    private final File fileName = new File("src/main/java/data/sampleMovies.json");

    public List<Movie> loadMovies() {
        try {
            if (fileName.exists()) {
                return mapper.readValue(fileName, new TypeReference<List<Movie>>() {});
            } else {
                System.out.println("Datei nicht gefunden: " + fileName.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }


    public List<Movie> findAll() {
        return new ArrayList<>(movies); // Kopie zurückgeben
    }

    public Optional<Movie> findById(int id) {
        return movies.stream()
                .filter(m -> m.getId() == id)
                .findFirst();
    }

    public void save(Movie movie) {
        // Wenn ID schon existiert -> aktualisieren
        findById(movie.getId()).ifPresentOrElse(existing -> {
            existing.setTitle(movie.getTitle());
            existing.setDirector(movie.getDirector());
            existing.setGenre(movie.getGenre());
            existing.setYear(movie.getYear());
            existing.setRating(movie.getRating());
        }, () -> {
            // neue ID zuweisen, wenn nicht gesetzt
            if (movie.getId() == 0) {
                movie.setId(generateNextId());
            }
            movies.add(movie);
        });

        saveToFile();
    }

    public boolean delete(int id) {
        boolean removed = movies.removeIf(m -> m.getId() == id);
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    private void saveToFile() {
        try {
            File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(String.valueOf(fileName))).toURI());
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, movies);
        } catch (Exception e) {
            System.err.println("Fehler beim Speichern von Filmen:");
            e.printStackTrace();
        }
    }

    private int generateNextId() {
        return movies.stream()
                .mapToInt(Movie::getId)
                .max()
                .orElse(0) + 1;
    }
}
