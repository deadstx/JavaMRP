package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Movie;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class JsonMovieRepository implements MovieRepository {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<Movie> loadMovies() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("sampleMovies.json")) {
            if (is != null) {
                return mapper.readValue(is, new TypeReference<List<Movie>>() {});
            } else {
                System.err.println("sampleMovies.json nicht gefunden!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList(); // falls Fehler: leere Liste zurück
    }
}
