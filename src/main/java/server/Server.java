package server;

import com.sun.net.httpserver.HttpServer;
import controller.*;
import models.Media;
import repository.*;
import service.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Server {

    public static void start() throws IOException {

        try {
            // ========================
            // DB CONNECTION
            // ========================
            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5431/mrp",
                    "mrp_user",
                    "test123"
            );

            // ========================
            // REPOSITORIES
            // ========================
            UserRepository userRepository = new UserRepository(conn);
            RegisterRepository registerRepository = new RegisterRepository(conn);
            ProfileRepository profileRepository = new ProfileRepository(conn);
            RatingRepository ratingRepository = new RatingRepository(conn);
            MediaRepository mediaRepository = new MediaRepository(conn);
            FavoriteRepository favoriteRepository = new FavoriteRepository(conn);
            CommentRepository commentRepository = new CommentRepository(conn);

            // ========================
            // SERVICES
            // ========================
            AuthService authService = new AuthService(userRepository);
            RegisterService registerService = new RegisterService(registerRepository);
            RatingService ratingService = new RatingService(ratingRepository);
            FavoriteService favoriteService = new FavoriteService(favoriteRepository, mediaRepository);
            ProfileService profileService = new ProfileService(profileRepository, ratingService, favoriteService);
            LeaderboardService leaderboardService = new LeaderboardService(ratingService, userRepository);
            MediaService mediaService = new MediaService(mediaRepository);
            CommentService commentService = new CommentService(commentRepository);
            RecommendationService recommendationService = new RecommendationService(mediaService, favoriteService);

            // ========================
            // HTTP SERVER
            // ========================
            HttpServer server = HttpServer.create(
                    new InetSocketAddress(8080),
                    0
            );

            // ========================
            // ROUTES
            // ========================

            // Landing Page
            server.createContext("/", new LandingPage());

            // AUTH
            server.createContext("/users/login", new LoginHandler(authService));
            server.createContext("/users/register", new RegisterHandler(registerService));

            //PROFILE
            server.createContext("/profile/users", new ProfileHandler(authService, profileService));

            // LEADERBOARD
            server.createContext("/leaderboard", new LeaderboardHandler(authService, leaderboardService));

            // RATINGS
            server.createContext("/ratings", new RatingHandler(authService, ratingService));

            // FAVORITES
            server.createContext("/favorites", new FavoriteHandler(authService, favoriteService));

            // COMMENTS
            server.createContext("/comments", new CommentHandler(authService, commentService));

            // RECOMMENDATIONS
            server.createContext("/recommendations", new RecommendationHandler(authService, recommendationService));


            // MEDIA
            server.createContext(
                    "/media",
                    new GenericMediaHandler<>(
                            authService,
                            mediaService,
                            Media.class,
                            "media"
                    )
            );

            // ========================
            // START SERVER
            // ========================
            server.setExecutor(null); // default executor
            server.start();

            System.out.println("Server läuft auf http://localhost:8080");

        } catch (SQLException e) {
            throw new RuntimeException("DB Verbindung fehlgeschlagen", e);
        }
    }
}
