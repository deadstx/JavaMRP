package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.UserRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    AuthService authService;


    @Test
    void isValidLogin_correctPassword_shouldReturnTrue() {
        String username = "max";
        String password = "secret123";

        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        when(userRepository.findPasswordHashByUsername(username))
                .thenReturn(hash);

        boolean result = authService.isValidLogin(username, password);

        assertTrue(result);
        verify(userRepository).findPasswordHashByUsername(username);
    }


    @Test
    void isValidLogin_wrongPassword_shouldReturnFalse() {
        String username = "max";

        String hash = BCrypt.hashpw("correctPassword", BCrypt.gensalt());

        when(userRepository.findPasswordHashByUsername(username))
                .thenReturn(hash);

        boolean result = authService.isValidLogin(username, "wrongPassword");

        assertFalse(result);
    }

    @Test
    void isValidLogin_userNotFound_shouldReturnFalse() {
        when(userRepository.findPasswordHashByUsername("unknown"))
                .thenReturn(null);

        boolean result = authService.isValidLogin("unknown", "pw");

        assertFalse(result);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        String username = "max";

        String token = authService.generateToken(username);

        assertNotNull(token);
    }


    @Test
    void verifyToken_validToken_shouldNotThrow() {
        String token = authService.generateToken("max");

        assertDoesNotThrow(() -> authService.verifyToken(token));
    }


    @Test
    void verifyToken_invalidToken_shouldThrowException() {
        String invalidToken = "this.is.not.a.jwt";

        assertThrows(Exception.class, () ->
                authService.verifyToken(invalidToken)
        );
    }


    @Test
    void getUserIdFromToken_shouldReturnUserId() {
        String username = "max";
        UUID userId = UUID.randomUUID();

        String token = authService.generateToken(username);

        when(userRepository.findIdByUsername(username))
                .thenReturn(userId);

        UUID result = authService.getUserIdFromToken(token);

        assertEquals(userId, result);
        verify(userRepository).findIdByUsername(username);
    }
}