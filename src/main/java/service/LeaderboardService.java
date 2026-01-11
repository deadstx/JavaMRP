package service;

import dto.LeaderboardUserDto;
import repository.UserRepository;

import java.util.List;


public class LeaderboardService {

    private final RatingService ratingService;
    private final UserRepository userRepository;

    public LeaderboardService(RatingService ratingService, UserRepository userRepository) {
        this.ratingService = ratingService;
        this.userRepository = userRepository;
    }

    /* ---------------------------------------------------
     * READ
     * --------------------------------------------------- */

    public List<LeaderboardUserDto> getTopUsers(int userCount) {
        List<LeaderboardUserDto> topUserIdList = ratingService.getTopUserIdList(userCount);
        return userRepository.enrichLeaderboardUsers(topUserIdList);
    }
}
