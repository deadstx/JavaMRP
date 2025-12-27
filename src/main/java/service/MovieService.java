package service;

import models.Movie;
import repository.MovieRepository;

import java.util.List;
import java.util.Optional;

public class MovieService implements MediaService<Movie> {

    private final MovieRepository repo;

    public MovieService(MovieRepository repo) {
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
