package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.RegisterRepository;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock
    RegisterRepository registerRepository;

    @InjectMocks
    RegisterService registerService;

    // ---------- Username Validation ----------

    @Test
    void validateUsername_validUsername_shouldReturnTrue() {
        assertTrue(registerService.validateUsername("valid_user123"));
    }

    @Test
    void validateUsername_tooShort_shouldReturnFalse() {
        assertFalse(registerService.validateUsername("ab"));
    }

    @Test
    void validateUsername_invalidCharacters_shouldReturnFalse() {
        assertFalse(registerService.validateUsername("user!name"));
    }

    @Test
    void validateUsername_nullOrBlank_shouldReturnFalse() {
        assertFalse(registerService.validateUsername(null));
        assertFalse(registerService.validateUsername(""));
        assertFalse(registerService.validateUsername("   "));
    }

    // ---------- Password Validation ----------

    @Test
    void validatePassword_validPassword_shouldReturnTrue() {
        assertTrue(registerService.validatePassword("CorrectPw1!"));
    }

    @Test
    void validatePassword_missingUppercase_shouldReturnFalse() {
        assertFalse(registerService.validatePassword("incorrect1!"));
    }

    @Test
    void validatePassword_missingSpecialChar_shouldReturnFalse() {
        assertFalse(registerService.validatePassword("MissingSpecial1"));
    }

    @Test
    void validatePassword_nullOrBlank_shouldReturnFalse() {
        assertFalse(registerService.validatePassword(null));
        assertFalse(registerService.validatePassword(""));
        assertFalse(registerService.validatePassword("   "));
    }

    // ---------- Register User ----------

    @Test
    void registerUser_userDoesNotExist_shouldCreateUser() throws SQLException {
        String username = "newUser";
        String password = "Strong1!A";

        when(registerRepository.userExists(username))
                .thenReturn(false);

        when(registerRepository.createUser(eq(username), anyString()))
                .thenReturn(true);

        boolean result = registerService.registerUser(username, password);

        assertTrue(result);
        verify(registerRepository).userExists(username);
        verify(registerRepository).createUser(eq(username), anyString());
    }

    @Test
    void registerUser_userAlreadyExists_shouldReturnFalse() throws SQLException {
        String username = "existingUser";

        when(registerRepository.userExists(username))
                .thenReturn(true);

        boolean result = registerService.registerUser(username, "Strong1!A");

        assertFalse(result);
        verify(registerRepository).userExists(username);
        verify(registerRepository, never()).createUser(any(), any());
    }

    @Test
    void registerUser_passwordShouldBeHashed() throws SQLException {
        String username = "hashTestUser";
        String password = "Strong1!A";

        when(registerRepository.userExists(username))
                .thenReturn(false);

        when(registerRepository.createUser(eq(username), anyString()))
                .thenAnswer(invocation -> {
                    String hashedPassword = invocation.getArgument(1);
                    return BCrypt.checkpw(password, hashedPassword);
                });

        boolean result = registerService.registerUser(username, password);

        assertTrue(result);
    }
}
