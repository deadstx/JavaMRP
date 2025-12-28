package service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MediaService<T> {
    Optional<T> findById(UUID id);
    List<T> findAll();
    void add(T item);
    boolean deleteById(UUID id);
}
