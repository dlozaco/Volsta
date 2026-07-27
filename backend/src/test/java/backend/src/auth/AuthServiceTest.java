package backend.src.auth;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import backend.src.auth.request.SignUpRequest;
import backend.src.manager.Manager;
import backend.src.manager.ManagerService;
import backend.src.user.Authorities;
import backend.src.user.AuthoritiesService;
import backend.src.user.User;
import backend.src.user.UserService;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthoritiesService authoritiesService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ManagerService managerService;

    private SignUpRequest signUpRequest;
    private Authorities adminRole;
    private Authorities managerRole;

    @BeforeEach
    void setUp() {
        adminRole = new Authorities();
        adminRole.setAuthority("ADMIN");

        managerRole = new Authorities();
        managerRole.setAuthority("MANAGER");

        signUpRequest = new SignUpRequest();
        signUpRequest.setUserName("testUser");
        signUpRequest.setPassword("Password123!");
        signUpRequest.setFirstName("Juan");
        signUpRequest.setLastName("Pérez");
        signUpRequest.setEmail("juan@example.com");
        signUpRequest.setPhoneNumber("123456789");
    }

    @Test
    void shouldCreateAdmin_ValidData() {
        signUpRequest.setAuthority("admin");

        when(passwordEncoder.encode(anyString())).thenReturn("EncodedPassword1!");
        when(authoritiesService.findByAuthoritiy("ADMIN")).thenReturn(adminRole);

        authService.createUser(signUpRequest);

        verify(userService, times(1)).createUser(any(User.class));
        verify(managerService, never()).create(any(Manager.class));
    }

    @Test
    void shouldCreateManager_ValidData() {
        signUpRequest.setAuthority("manager");

        when(passwordEncoder.encode(anyString())).thenReturn("EncodedPassword1!");
        when(authoritiesService.findByAuthoritiy("MANAGER")).thenReturn(managerRole);

        authService.createUser(signUpRequest);

        verify(userService, times(1)).createUser(any(User.class));
        verify(managerService, times(1)).create(any(Manager.class));
    }

    @Test
    void shouldEncodePassword_WhenCreatingUser() {
        signUpRequest.setAuthority("admin");

        when(passwordEncoder.encode("Password123!")).thenReturn("EncodedPassword1!");
        when(authoritiesService.findByAuthoritiy("ADMIN")).thenReturn(adminRole);

        authService.createUser(signUpRequest);

        verify(passwordEncoder, times(1)).encode("Password123!");
    }

    @Test
    void shouldSetManagerDetails_WhenCreatingManager() {
        signUpRequest.setAuthority("manager");

        when(passwordEncoder.encode(anyString())).thenReturn("EncodedPassword1!");
        when(authoritiesService.findByAuthoritiy("MANAGER")).thenReturn(managerRole);

        authService.createUser(signUpRequest);

        verify(managerService, times(1)).create(argThat(manager ->
                manager.getEmail().equals("juan@example.com") &&
                        manager.getName().equals("Juan") &&
                        manager.getSurname().equals("Pérez") &&
                        manager.getPhoneNumber().equals("123456789")
        ));
    }

    @Test
    void shouldThrowException_WhenInvalidAuthority() {
        signUpRequest.setAuthority("invalid");

        when(passwordEncoder.encode(anyString())).thenReturn("EncodedPassword1!");

        assertDoesNotThrow(() -> authService.createUser(signUpRequest));
        verify(userService, never()).createUser(any(User.class));
        verify(managerService, never()).create(any(Manager.class));
    }
}