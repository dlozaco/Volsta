package backend.src.manager;

import backend.src.manager.helpers.ProfileHelpers;
import backend.src.user.User;
import backend.src.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final UserService userService;
    private final ManagerService managerService;

    @Autowired
    public ProfileController(UserService userService, ManagerService managerService) {
        this.userService = userService;
        this.managerService = managerService;
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

        managerService.create(manager);
        ManagerProfile managerProfile = ProfileHelpers.managerToProfile(manager);

        return ResponseEntity.ok(managerProfile);
    }
}
