package service;

import models.Series;
import repository.JsonSeriesRepository;

import java.util.List;
import java.util.Optional;

public class SeriesService implements MediaService<Series> {

    private final JsonSeriesRepository repo;

    public SeriesService(JsonSeriesRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Series> findById(int id) {
        return repo.findById(id);
    }

    @Override
    public List<Series> findAll() {
        return repo.findAll();
    }

    @Override
    public void add(Series series) {
        repo.save(series);
    }

    @Override
    public boolean deleteById(int id) {
        return repo.delete(id);
    }
}
