package service;

import dto.MediaWithRatingDto;
import models.Media;
import models.MediaFilter;
import models.MediaType;
import repository.MediaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MediaService {

    private final MediaRepository repo;

    public MediaService(MediaRepository repo) {
        this.repo = repo;
    }

    // ========================
    // FIND BY ID
    // ========================
    public Optional<Media> findById(UUID id) {
        return repo.findById(id);
    }

    // ========================
    // FIND ALL (optional nach Typ)
    // ========================
    public List<Media> findAll(Optional<MediaType> mediaType) {
        return repo.findAll(mediaType);
    }

    public List<MediaWithRatingDto> findAllWithRating() {
        System.out.println("SERVICE: findAllWithRating()");
        return repo.findAllWithRating();
    }

    public List<MediaWithRatingDto> findReccommendedMedias(String favoriteGenre, int recommendationCount) {
        return repo.findReccommendedMedias(favoriteGenre, recommendationCount);
    }


    public List<Media> findAll() {
        return repo.findAll(Optional.empty());
    }

    // ========================
    // CREATE / UPDATE
    // ========================
    public boolean save(Media media, UUID currentUserId) {
        if (media.getId() != null) {
            Optional<Media> existingOpt = repo.findById(media.getId());
            if (existingOpt.isEmpty()) return false;

            Media existing = existingOpt.get();
            if (!existing.getCreatorId().equals(currentUserId)) return false;

            media.setCreatorId(existing.getCreatorId());
        } else {
            media.setCreatorId(currentUserId);
        }

        repo.save(media);
        return true;
    }

    // ========================
    // FILTER METHODS
    // ========================
    public List<Media> filterMedia(MediaFilter filterType, Object value) {
        return repo.findByFilter(filterType, value);
    }



    // ========================
    // DELETE
    // ========================
    public boolean delete(UUID mediaId, UUID currentUserId) {
        return repo.delete(mediaId, currentUserId);
    }
}
