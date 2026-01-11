package service;

import dto.MediaWithRatingDto;
import models.Media;
import models.MediaFilter;
import models.MediaType;
import repository.MediaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MediaService {

    private final MediaRepository repo;

    public MediaService(MediaRepository repo) {
        this.repo = repo;
    }

    // ========================
    // FIND BY ID
    // ========================
    public MediaWithRatingDto findById(UUID id) {
        return repo.findByIdWithRating(id);
    }

    public boolean existsById(UUID mediaId) {
        return repo.existsById(mediaId);
    }

    // ========================
    // FIND ALL (optional nach Typ)
    // ========================
    public List<Media> findAll(Optional<MediaType> mediaType) {
        return repo.findAll(mediaType);
    }

    public List<MediaWithRatingDto> findAllWithRating() {
        return repo.findAllWithRating();
    }

    public List<MediaWithRatingDto> findRecommendedMedias(String favoriteGenre, int recommendationCount) {
        return repo.findRecommendedMedias(favoriteGenre, recommendationCount);
    }

    // überlegen ob noch nützlich (ich lasse mal drinnen)
    public List<Media> findAll() {
        return repo.findAll(Optional.empty());
    }

    // ========================
    // CREATE
    // ========================
    public boolean create(Media media, UUID currentUserId) {
        media.setCreatorId(currentUserId);
        return repo.insert(media);
    }

    // ========================
    // UPDATE
    // ========================
    public boolean update(Media media, UUID currentUserId) {
        if (media.getId() == null) {
            return false;
        }

        if(!media.getCreatorId().equals(currentUserId)) return false;

        // müssen serverside gesetzt werden sonst security issue
        media.setCreatorId(media.getCreatorId());

        return repo.update(media);
    }

    // ========================
    // FILTER METHODS
    // ========================
    public List<Media> filterMedia(MediaFilter filterType, Object value) {
        return repo.findByFilter(filterType, value);
    }

    public boolean existsByTitle(String title) {
        return repo.existsByTitle(title);
    }

    // ========================
    // DELETE
    // ========================
    public boolean delete(UUID mediaId, UUID currentUserId) {
        return repo.delete(mediaId, currentUserId);
    }
}
