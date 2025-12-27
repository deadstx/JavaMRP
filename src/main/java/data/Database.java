package data;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

    public static void setupDB() {
        String url = "jdbc:postgresql://localhost:5431/mrp";
        String user = "mrp_user";
        String password = "test123";

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
