package main;
import server.Server;
import data.Database;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Database.setupDB();
        Server.start();
    }
}