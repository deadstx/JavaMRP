package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Series;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JsonSeriesRepository {

    private final ObjectMapper mapper = new ObjectMapper();
    private final File fileName = new File("src/main/java/data/sampleSeries.json");
    private List<Series> series;

    public JsonSeriesRepository() {
        this.series = loadSeries();
    }

    public List<Series> loadSeries() {
        try {
            if (fileName.exists()) {
                return mapper.readValue(fileName, new TypeReference<List<Series>>() {});
            } else {
                System.out.println("Datei nicht gefunden: " + fileName.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Series> findAll() {
        return new ArrayList<>(series); // Kopie zurückgeben
    }

    public Optional<Series> findById(int id) {
        return series.stream()
                .filter(m -> m.getId() == id)
                .findFirst();
    }

    public void save(Series seriesToSave) {
        findById(seriesToSave.getId()).ifPresentOrElse(existing -> {
            existing.setTitle(seriesToSave.getTitle());
            existing.setDirector(seriesToSave.getDirector());
            existing.setGenre(seriesToSave.getGenre());
            existing.setYear(seriesToSave.getYear());
            existing.setRating(seriesToSave.getRating());
        }, () -> {
            if (seriesToSave.getId() == 0) {
                seriesToSave.setId(generateNextId());
            }
            series.add(seriesToSave);
        });

        saveToFile();
    }

    public boolean delete(int id) {
        boolean removed = series.removeIf(m -> m.getId() == id);
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    private void saveToFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(fileName, series);
        } catch (Exception e) {
            System.err.println("Fehler beim Speichern von Serien:");
            e.printStackTrace();
        }
    }

    private int generateNextId() {
        return series.stream()
                .mapToInt(Series::getId)
                .max()
                .orElse(0) + 1;
    }
}
