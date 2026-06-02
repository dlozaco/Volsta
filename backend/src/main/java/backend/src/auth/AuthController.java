package backend.src.auth;

import backend.src.auth.request.SignUpRequest;
import backend.src.auth.response.MessageResponse;
import backend.src.exceptions.ValidationErrorResponse;
import backend.src.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService,
                          AuthService authService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest){
        if (userService.existsUser(signUpRequest.getUserName()).equals(true)) {
            Map<String, String> fieldErrors = Map.of("userName", "Username is already taken");
            return ResponseEntity.badRequest().body(new ValidationErrorResponse("Validation failed", fieldErrors));
        }
        authService.createUser(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

    }
}
