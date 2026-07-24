package backend.src.manager;

import backend.src.manager.helpers.ProfileHelpers;
import backend.src.user.User;
import backend.src.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
