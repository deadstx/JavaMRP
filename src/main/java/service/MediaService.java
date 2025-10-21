package service;

import java.util.List;
import java.util.Optional;

public interface MediaService<T> {
    Optional<T> findById(int id);
    List<T> findAll();
    void add(T item);
    boolean deleteById(int id);
}
