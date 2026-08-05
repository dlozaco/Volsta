package backend.src.auth;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.src.auth.request.LogInRequest;
import backend.src.auth.request.SignUpRequest;
import backend.src.auth.response.MessageResponse;
import backend.src.configuration.jwt.JwtUtil;
import backend.src.configuration.jwt.TokenBlacklistService;
import backend.src.exceptions.ResourceNotFoundException;
import backend.src.exceptions.ValidationErrorResponse;
import backend.src.manager.Manager;
import backend.src.manager.ManagerService;
import backend.src.user.User;
import backend.src.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final ManagerService managerService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userService.existsUser(signUpRequest.getUserName()).equals(true)) {
            Map<String, String> fieldErrors = Map.of("userName", "Username is already taken");
            return ResponseEntity.badRequest().body(new ValidationErrorResponse("Validation failed", fieldErrors));
        }
        authService.createUser(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> logInUser(@RequestBody LogInRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(loginRequest.getUsername());

        User user = userService.findUserByName(loginRequest.getUsername());

        String firstName = "";
        String lastName = "";

        if (user.hasAuthority("MANAGER")) {
            try {
                Manager manager = managerService.findByUserId(user.getId());
                firstName = manager.getName();
                lastName = manager.getSurname();
            } catch (ResourceNotFoundException e) {
                // Manager not found, leave names empty
            }
        }

        return ResponseEntity.ok(LoginResponse.builder()
                .token(jwt)
                .username(user.getUsername())
                .firstName(firstName)
                .lastName(lastName)
                .authority(user.getAuthority().getAuthority())
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logOutUser(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            tokenBlacklistService.blacklist(authorizationHeader.substring(7));
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findUserByName(userDetails.getUsername());

        String firstName = "";
        String lastName = "";

        if (user.hasAuthority("MANAGER")) {
            try {
                Manager manager = managerService.findByUserId(user.getId());
                firstName = manager.getName();
                lastName = manager.getSurname();
            } catch (ResourceNotFoundException e) {
                // Manager not found, leave names empty
            }
        }

        return ResponseEntity.ok(LoginResponse.builder()
                .username(user.getUsername())
                .firstName(firstName)
                .lastName(lastName)
                .authority(user.getAuthority().getAuthority())
                .build());
    }
}
