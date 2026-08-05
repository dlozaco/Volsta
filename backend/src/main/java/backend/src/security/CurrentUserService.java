package backend.src.security;

import backend.src.exceptions.ResourceNotFoundException;
import backend.src.manager.Manager;
import backend.src.manager.ManagerRepository;
import backend.src.user.User;
import backend.src.user.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Provides the currently authenticated user / manager from the SecurityContext.
 * Used by services to enforce ownership checks.
 */
@Service
public class CurrentUserService {

    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;

    public CurrentUserService(UserRepository userRepository, ManagerRepository managerRepository) {
        this.userRepository = userRepository;
        this.managerRepository = managerRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal() instanceof String) {
            throw new AccessDeniedException("Authentication required");
        }
        String username = authentication.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "name", username));
    }

    public boolean hasRole(String authority) {
        return getCurrentUser().hasAuthority(authority);
    }

    public boolean hasAnyRole(String... authorities) {
        return getCurrentUser().hasAnyAuthority(authorities);
    }

    public Manager getCurrentManager() {
        User user = getCurrentUser();
        if (!user.hasAuthority("MANAGER")) {
            throw new AccessDeniedException("Manager role required");
        }
        return managerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager", "userId", user.getId()));
    }
}
