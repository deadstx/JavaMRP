package service;

import models.Movie;
import repository.MovieRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MovieService implements MediaService<Movie> {

    private final MovieRepository repo;

    public MovieService(MovieRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Movie> findById(UUID id) {
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
    public boolean deleteById(UUID id, UUID currentUserId) {
        return repo.delete(id, currentUserId);
    }
}
