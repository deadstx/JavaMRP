package service;

import models.Movie;
import repository.JsonMovieRepository;

import java.util.List;
import java.util.Optional;

public class MovieService implements MediaService<Movie> {

    private final JsonMovieRepository repo;

    public MovieService(JsonMovieRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Movie> findById(int id) {
        return repo.findById(id);
    }

    @Override
    public List<Movie> findAll() {
        return repo.findAll();
    }

    @Override
    public void add(Movie movie) {
        repo.save(movie);
    }

    @Override
    public boolean deleteById(int id) {
        return repo.delete(id);
    }
}
