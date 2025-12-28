package data;

import java.sql.Connection;
import java.sql.DriverManager;

import data.DatabaseConfig;

public class Database {

    public static void setupDB() {
        String url = DatabaseConfig.URL;
        String user = DatabaseConfig.USER;
        String password = DatabaseConfig.PASSWORD;

        testDB(url, user, password);
    }

    public static void testDB(String url, String user, String password) {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Database - Verbindung erfolgreich!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
