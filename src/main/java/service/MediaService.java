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
    public MediaWithRatingDto findById(UUID id) {
        return repo.findByIdWithRating(id);
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
// CREATE
// ========================
    public boolean create(Media media, UUID currentUserId) {

        media.setCreatorId(currentUserId);
        repo.save(media);
        return true;
    }

    // ========================
// UPDATE
// ========================
    public boolean update(Media media, UUID currentUserId) {
        if (media.getId() == null) {
            // Ohne ID kann man nichts updaten
            return false;
        }

        MediaWithRatingDto existingDto = repo.findByIdWithRating(media.getId());
        if (existingDto == null) return false; // Medium existiert nicht

        Media existing = existingDto.getMedia();
        if (!existing.getCreatorId().equals(currentUserId)) return false; // Kein Recht, fremdes Medium zu ändern

        // ID und CreatorId bleiben unverändert
        media.setCreatorId(existing.getCreatorId());

        repo.save(media);
        return true;
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
