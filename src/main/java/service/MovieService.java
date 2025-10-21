package service;

import models.Movie;
import repository.MovieRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovieService {

    private final List<Movie> movieList; // In-Memory-Cache für Schreiboperationen

    public MovieService(MovieRepository repository) {
        this.movieList = new ArrayList<>(repository.loadMovies());
    }

    public List<Movie> getAllMovies() {
        return new ArrayList<>(movieList); // defensive copy
    }

    public void addMovie(Movie movie) {
        movieList.add(movie);
    }

    public boolean deleteMovieById(int id) {
        return movieList.removeIf(m -> m.getId() == id);
    }

    public Optional<Movie> findMovieById(int id) {
        return movieList.stream().filter(m -> m.getId() == id).findFirst();
    }


}
