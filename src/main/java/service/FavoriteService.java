package service;

import models.Favorite;
import models.Media;
import repository.FavoriteRepository;
import repository.MediaRepository;
import service.AuthService;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class FavoriteService {

    private final FavoriteRepository favoriteRepo;
    private final MediaRepository mediaRepo;

    public FavoriteService(FavoriteRepository favoriteRepo,
                           MediaRepository mediaRepo) {
        this.favoriteRepo = favoriteRepo;
        this.mediaRepo = mediaRepo;
    }

    public List<Media> getUserFavorites(UUID userId) {
        List<UUID> mediaIds = favoriteRepo.findFavoritesByUser(userId);
        System.out.println("Service geht");
        return mediaRepo.findByIdList(mediaIds);
    }

    public boolean removeFromFavorites(UUID mediaId, UUID currentUserId) {
       return favoriteRepo.removeFromFavorites(mediaId, currentUserId);
    }

    public boolean exists(UUID mediaId, UUID currentUserId) {
        return favoriteRepo.checkIfExists(mediaId, currentUserId);
    }

    public boolean markAsFavorite(UUID mediaId, UUID currentUserId) {
        return favoriteRepo.markAsFavorite(mediaId, currentUserId);
    }
}

