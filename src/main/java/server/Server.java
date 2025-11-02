package server;

import com.sun.net.httpserver.HttpServer;
import controller.GenericMediaHandler;
import controller.LandingPage;
import controller.LoginHandler;
import models.Movie;
import models.Series;
import repository.JsonMovieRepository;
import repository.JsonSeriesRepository;
import service.MovieService;
import service.SeriesService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public static void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        LoginHandler loginHandler = new LoginHandler();
        LandingPage landingPage = new LandingPage();

        // SERVER ROUTES
        server.createContext("/", landingPage);
        server.createContext("/login", loginHandler);


        // MEDIA ROUTES
        server.createContext("/movies", new GenericMediaHandler<>(
                new MovieService(new JsonMovieRepository()),
                Movie.class, // wegen type Erasure
                "movies"
        ));

        server.createContext("/series", new GenericMediaHandler<>(
                new SeriesService(new JsonSeriesRepository()), // Repository nicht vergessen!
                Series.class, // wegen type Erasure
                "series"
        ));

        server.setExecutor(null);
        server.start();
        System.out.println("Server läuft auf Port 8080");
    }
}
