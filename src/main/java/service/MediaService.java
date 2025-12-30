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
    public void save(Media media, UUID currentUserId) {

        // Sicherheits-Check
        if (media.getId() != null) {
            // Update → prüfen ob User Eigentümer ist
            Optional<Media> existing = repo.findById(media.getId());

            if (existing.isEmpty() ||
                    !existing.get().getCreatorId().equals(currentUserId)) {
                throw new SecurityException("Kein Zugriff auf dieses Medium");
            }
        } else {
            // Insert → Creator setzen
            media.setCreatorId(currentUserId);
        }

        repo.save(media);
    }

    // ========================
    // DELETE
    // ========================
    public boolean delete(UUID mediaId, UUID currentUserId) {
        return repo.delete(mediaId, currentUserId);
    }
}
