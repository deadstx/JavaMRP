package server;

import com.sun.net.httpserver.HttpServer;
import controller.GenericMediaHandler;
import controller.LandingPage;
import controller.LoginHandler;
import models.Movie;
import repository.JsonMovieRepository;
import service.MovieService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public static void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);


        LoginHandler loginHandler = new LoginHandler();

        LandingPage landingPage = new LandingPage();

        server.createContext("/", landingPage);

        server.createContext("/login", loginHandler);

        server.createContext("/movies", new GenericMediaHandler<>(
                new MovieService(new JsonMovieRepository()), // Repository nicht vergessen!
                Movie.class,
                "movies"
        ));




        server.setExecutor(null);
        server.start();
        System.out.println("Server läuft auf Port 8080");
    }
}
