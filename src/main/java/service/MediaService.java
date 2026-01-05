package service;

import models.Media;
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
    public Optional<Media> findById(UUID id) {
        return repo.findById(id);
    }

    // ========================
    // FIND ALL (optional nach Typ)
    // ========================
    public List<Media> findAll(Optional<MediaType> mediaType) {
        return repo.findAll(mediaType);
    }

    // Komfort-Methode ohne Filter
    public List<Media> findAll() {
        return repo.findAll(Optional.empty());
    }

    // ========================
    // CREATE / UPDATE
    // ========================
    public boolean save(Media media, UUID currentUserId) {
        // UPDATE
        if (media.getId() != null) {
            Optional<Media> existingOpt = repo.findById(media.getId());
            // Medium existiert nicht
            if (existingOpt.isEmpty()) {
                return false;
            }

            Media existing = existingOpt.get();

            // Kein Zugriff
            if (!existing.getCreatorId().equals(currentUserId)) {
                return false;
            }

            // Creator darf NICHT überschrieben werden
            media.setCreatorId(existing.getCreatorId());

        } else {
            // CREATE
            media.setCreatorId(currentUserId);
        }

        repo.save(media);
        return true;
    }




    // ========================
    // DELETE
    // ========================
    public boolean delete(UUID mediaId, UUID currentUserId) {
        return repo.delete(mediaId, currentUserId);
    }
}
