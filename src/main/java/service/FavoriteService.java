package service;

import models.Favorite;
import models.Media;
import repository.FavoriteRepository;
import repository.MediaRepository;

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
        return mediaRepo.findByIdList(mediaIds);
    }
}

