package service;

import dto.UserProfileDto;
import models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.ProfileRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    ProfileRepository profileRepository;

    @Mock
    RatingService ratingService;

    @Mock
    FavoriteService favoriteService;

    @InjectMocks
    ProfileService profileService;

    @Test
    void getProfileData_userExists_shouldReturnProfileDto() {
        UUID userId = UUID.randomUUID();

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getUsername()).thenReturn("testUser");

        when(profileRepository.fetchUserProfile(userId))
                .thenReturn(user);

        when(ratingService.getRatingCountByUser(userId))
                .thenReturn(5);

        when(favoriteService.getFavoriteGenre(userId))
                .thenReturn("Action");

        UserProfileDto result = profileService.getProfileData(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testUser", result.getUsername());
        assertEquals(5, result.getRatingCount());
        assertEquals("Action", result.getFavoriteGenre());

        verify(profileRepository).fetchUserProfile(userId);
        verify(ratingService).getRatingCountByUser(userId);
        verify(favoriteService).getFavoriteGenre(userId);
    }

    @Test
    void getProfileData_userNotFound_shouldReturnNull() {
        UUID userId = UUID.randomUUID();

        when(profileRepository.fetchUserProfile(userId))
                .thenReturn(null);

        UserProfileDto result = profileService.getProfileData(userId);

        assertNull(result);

        verify(profileRepository).fetchUserProfile(userId);
        verifyNoInteractions(ratingService);
        verifyNoInteractions(favoriteService);
    }

    @Test
    void getProfileData_noRatingsAndNoFavoriteGenre() {
        UUID userId = UUID.randomUUID();

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getUsername()).thenReturn("plainUser");

        when(profileRepository.fetchUserProfile(userId))
                .thenReturn(user);

        when(ratingService.getRatingCountByUser(userId))
                .thenReturn(0);

        when(favoriteService.getFavoriteGenre(userId))
                .thenReturn(null);

        UserProfileDto result = profileService.getProfileData(userId);

        assertNotNull(result);
        assertEquals(0, result.getRatingCount());
        assertNull(result.getFavoriteGenre());
    }
}
