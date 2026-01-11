package service;

import dto.LeaderboardUserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.UserRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    RatingService ratingService;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    LeaderboardService leaderboardService;

    @Test
    void getTopUsers_shouldReturnEnrichedLeaderboardUsers() {
        int userCount = 3;

        List<LeaderboardUserDto> topUsers = List.of(
                new LeaderboardUserDto(UUID.randomUUID(), 10),
                new LeaderboardUserDto(UUID.randomUUID(), 8),
                new LeaderboardUserDto(UUID.randomUUID(), 5)
        );

        List<LeaderboardUserDto> enrichedUsers = List.of(
                new LeaderboardUserDto(UUID.randomUUID(), 10),
                new LeaderboardUserDto(UUID.randomUUID(), 8),
                new LeaderboardUserDto(UUID.randomUUID(), 5)
        );

        when(ratingService.getTopUserIdList(userCount))
                .thenReturn(topUsers);

        when(userRepository.enrichLeaderboardUsers(topUsers))
                .thenReturn(enrichedUsers);

        List<LeaderboardUserDto> result =
                leaderboardService.getTopUsers(userCount);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(enrichedUsers, result);

        verify(ratingService).getTopUserIdList(userCount);
        verify(userRepository).enrichLeaderboardUsers(topUsers);
    }

    @Test
    void getTopUsers_emptyResult_shouldReturnEmptyList() {
        int userCount = 5;

        List<LeaderboardUserDto> emptyList = List.of();

        when(ratingService.getTopUserIdList(userCount))
                .thenReturn(emptyList);

        when(userRepository.enrichLeaderboardUsers(emptyList))
                .thenReturn(emptyList);

        List<LeaderboardUserDto> result =
                leaderboardService.getTopUsers(userCount);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(ratingService).getTopUserIdList(userCount);
        verify(userRepository).enrichLeaderboardUsers(emptyList);
    }

    @Test
    void getTopUsers_shouldDelegateCorrectly() {
        int userCount = 1;

        List<LeaderboardUserDto> topUsers =
                List.of(new LeaderboardUserDto(UUID.randomUUID(), 42));

        when(ratingService.getTopUserIdList(userCount))
                .thenReturn(topUsers);

        when(userRepository.enrichLeaderboardUsers(any()))
                .thenReturn(topUsers);

        leaderboardService.getTopUsers(userCount);

        verify(ratingService).getTopUserIdList(userCount);
        verify(userRepository).enrichLeaderboardUsers(topUsers);
        verifyNoMoreInteractions(ratingService, userRepository);
    }
}
