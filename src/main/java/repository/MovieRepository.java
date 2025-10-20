package repository;

import models.Movie;
import java.util.List;

public interface MovieRepository {
    List<Movie> loadMovies();
}
