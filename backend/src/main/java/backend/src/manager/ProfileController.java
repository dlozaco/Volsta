package backend.src.manager;

import backend.src.auth.request.PasswordChangeRequest;
import backend.src.manager.helpers.ProfileHelpers;
import backend.src.user.User;
import backend.src.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final UserService userService;
    private final ManagerService managerService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ProfileController(UserService userService, ManagerService managerService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.managerService = managerService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/manager")
    public ResponseEntity<?> getManagerProfile(Authentication authentication){
        String username = authentication.getName();
        User user = userService.findUserByName(username);
        Manager manager = managerService.findByUserId(user.getId());
        ManagerProfile managerProfile = ProfileHelpers.managerToProfile(manager);

        return ResponseEntity.ok(managerProfile);
    }

    @PutMapping("/manager")
    public ResponseEntity<?> updateManagerProfile(Authentication authentication, @RequestBody Map<String, String> body){
        String username = authentication.getName();
        User user = userService.findUserByName(username);
        Manager manager = managerService.findByUserId(user.getId());

        if (body.containsKey("name")) manager.setName(body.get("name"));
        if (body.containsKey("surname")) manager.setSurname(body.get("surname"));
        if (body.containsKey("email")) manager.setEmail(body.get("email"));
        if (body.containsKey("phoneNumber")) manager.setPhoneNumber(body.get("phoneNumber"));
        if (body.containsKey("photoUrl")) manager.setPhotoUrl(body.get("photoUrl"));

        managerService.create(manager);
        ManagerProfile managerProfile = ProfileHelpers.managerToProfile(manager);

        return ResponseEntity.ok(managerProfile);
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(Authentication authentication, @Valid @RequestBody PasswordChangeRequest body){
        String username = authentication.getName();
        User user = userService.findUserByName(username);

        if (!passwordEncoder.matches(body.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(400).body(Map.of("message", "Current password is incorrect"));
        }

        user.setPassword(passwordEncoder.encode(body.getNewPassword()));
        userService.createUser(user);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }
}
