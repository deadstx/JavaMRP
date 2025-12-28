package service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.mindrot.jbcrypt.BCrypt;
import repository.UserRepository;

import java.util.Date;

public class AuthService {

    private static final String SECRET = "super_secret_key_123";
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    private final UserRepository userRepository;

    // 🔑 Repository wird injected
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Login prüfen
    public boolean isValidLogin(String username, String password) {
        String storedHash = userRepository.findPasswordHashByUsername(username);

        if (storedHash == null) {
            return false;
        }
        return BCrypt.checkpw(password, storedHash);
    }

    // Token erstellen
    public String generateToken(String username) {
        return JWT.create()
                .withIssuer("mrp-api")
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600_000)) // 1 Stunde
                .sign(algorithm);
    }

    // Token verifizieren
    public void verifyToken(String token) {
        JWT.require(algorithm)
                .withIssuer("mrp-api")
                .build()
                .verify(token);
    }

}
