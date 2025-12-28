package service;

import com.auth0.jwt.algorithms.Algorithm;
import org.mindrot.jbcrypt.BCrypt;
import repository.RegisterRepository;

import java.sql.SQLException;

public class RegisterService {

    private static final String SECRET = "super_secret_key_123";
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    private final RegisterRepository registerRepo;

    // 🔑 Repository wird injected
    public RegisterService(RegisterRepository createUser) {
        this.registerRepo = createUser;
    }

    // Benutzer registrieren
    public boolean registerUser(String username, String password) throws SQLException {
        // Prüfen, ob Benutzer bereits existiert
        if(!registerRepo.userExists(username)) {
            // Passwort hashen
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Benutzer speichern
            if( registerRepo.createUser(username, hashedPassword)) {
                return true;
            }
        }

        return false;
    }
}
