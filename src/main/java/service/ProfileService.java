package service;

import models.User;
import repository.ProfileRepository;
import repository.RegisterRepository;

import java.sql.SQLException;
import java.util.UUID;

public class ProfileService {

    private final ProfileRepository profileRepo;

    public ProfileService(ProfileRepository getUserData) {
        this.profileRepo = getUserData;
    }

    // Benutzer registrieren
    public User getProfileData(UUID currentUserId) throws SQLException {
        // repo aufrufen -> repo.fetchUserData
        // HIER NOCH DIE AKTUELLE USER ID LADEN

        return (profileRepo.fetchUserProfile(currentUserId));
    }
}
