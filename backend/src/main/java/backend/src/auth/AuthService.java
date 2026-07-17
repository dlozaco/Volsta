package backend.src.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import backend.src.auth.request.SignUpRequest;
import backend.src.manager.Manager;
import backend.src.manager.ManagerService;
import backend.src.user.Authorities;
import backend.src.user.AuthoritiesService;
import backend.src.user.User;
import backend.src.user.UserService;
import jakarta.validation.Valid;

@Service
@Validated
public class AuthService {

    private final PasswordEncoder encoder;
    private final AuthoritiesService authoritiesService;
    private final ManagerService managerService;
    private final UserService userService;

    public AuthService(PasswordEncoder encoder, AuthoritiesService authoritiesService, ManagerService managerService, UserService userService) {
        this.encoder = encoder;
        this.authoritiesService = authoritiesService;
        this.managerService = managerService;
        this.userService = userService;
    }

    @Transactional
    public void createUser(@Valid SignUpRequest request) {
        User user = new User();
        user.setUsername(request.getUserName());
        user.setPassword(encoder.encode(request.getPassword()));
        String strRoles = request.getAuthority();
        Authorities role;

        switch (strRoles.toLowerCase()){
            case "admin":
                role = authoritiesService.findByAuthoritiy("ADMIN");
                user.setAuthority(role);
                userService.createUser(user);
                break;
            case "manager":
                role = authoritiesService.findByAuthoritiy("MANAGER");
                user.setAuthority(role);
                userService.createUser(user);
                Manager manager = new Manager();
                manager.setUser(user);
                manager.setEmail(request.getEmail());
                manager.setName(request.getFirstName());
                manager.setSurname(request.getLastName());
                manager.setPhoneNumber(request.getPhoneNumber());
                managerService.create(manager);
                break;
        }

    }
}
