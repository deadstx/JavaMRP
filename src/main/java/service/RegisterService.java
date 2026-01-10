package service;

import com.auth0.jwt.algorithms.Algorithm;
import org.mindrot.jbcrypt.BCrypt;
import repository.RegisterRepository;

import java.sql.SQLException;

public class RegisterService {

    private static final String SECRET = "super_secret_key_123";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,20}$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$";

    private final RegisterRepository registerRepo;

    public RegisterService(RegisterRepository createUser) {
        this.registerRepo = createUser;
    }

    public boolean validateUsername(String username) {
        System.out.println("user: " + username);
        if(username == null || username.isBlank()) return false;
        return username.matches(USERNAME_REGEX);
    }

    public boolean validatePassword(String password) {
        System.out.println("pw: " + password);
        if(password == null || password.isBlank()) return false;
        return password.matches(PASSWORD_REGEX);
    }

    public boolean registerUser(String username, String password) throws SQLException {
        if(registerRepo.userExists(username)) return false;

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        return registerRepo.createUser(username, hashedPassword);
    }

}
