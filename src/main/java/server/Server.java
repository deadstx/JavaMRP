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

            // ========================
            // SERVICES
            // ========================
            AuthService authService = new AuthService(userRepository);
            RegisterService registerService = new RegisterService(registerRepository);
            ProfileService profileService = new ProfileService(profileRepository);
            RatingService ratingService = new RatingService(ratingRepository);
            MediaService mediaService = new MediaService(mediaRepository);

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
            server.createContext("/login", new LoginHandler(authService));
            server.createContext("/register", new RegisterHandler(registerService));
            server.createContext("/profile", new ProfileHandler(authService, profileService));

            // RATINGS
            server.createContext("/ratings", new RatingHandler(authService, ratingService));

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
