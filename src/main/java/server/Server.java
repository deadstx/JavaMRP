package server;

import com.sun.net.httpserver.HttpServer;
import controller.*;
import models.Movie;
//import models.Series;


import repository.MovieRepository;
import repository.RegisterRepository;
import repository.ProfileRepository;
//import repository.SeriesRepository;
import repository.UserRepository;


import service.AuthService;
import service.MovieService;
import service.ProfileService;
import service.RegisterService;
//import service.SeriesService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Server {


    public static void start() throws IOException {
        try {
            // 🔌 DB Connection
            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5431/mrp",
                    "mrp_user",
                    "test123"
            );

            // 🔐 AUTH
            UserRepository userRepository = new UserRepository(conn);
            RegisterRepository registerRepository = new RegisterRepository(conn);
            ProfileRepository profileRepository = new ProfileRepository(conn);

            AuthService authService = new AuthService(userRepository);
            RegisterService registerService = new RegisterService(registerRepository);
            ProfileService profileService = new ProfileService(profileRepository);

            // 🎬 MEDIA
            MovieRepository movieRepository = new MovieRepository(conn);
            //SeriesRepository seriesRepository = new SeriesRepository(conn);
            // GameRepository gameRepository = new GameRepository(conn);


            MovieService movieService = new MovieService(movieRepository);
         //   SeriesService seriesService = new SeriesService(seriesRepository);
            //   GameService gameService = new GameService(gameService);
            

            // 🌐 HTTP SERVER
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/", new LandingPage());

            // AUTH
            server.createContext("/login", new LoginHandler(authService));
            server.createContext("/register", new RegisterHandler(registerService));
            server.createContext("/profile", new ProfileHandler(authService, profileService));

            // MEDIA
            server.createContext("/movies",
                    new GenericMediaHandler<>(
                            authService,
                            movieService,
                            Movie.class,
                            "movies"
                    )
            );

       /*     server.createContext("/series",
                    new GenericMediaHandler<>(
                            authService,
                            seriesService,
                            Series.class,
                            "series"
                    )
            );
*/
            server.setExecutor(null);
            server.start();
            System.out.println("Server läuft auf Port 8080");

        } catch (SQLException e) {
            throw new RuntimeException("DB Verbindung fehlgeschlagen", e);
        }
    }
}
