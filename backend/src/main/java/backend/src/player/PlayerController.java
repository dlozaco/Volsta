package backend.src.player;

import backend.src.manager.Manager;
import backend.src.player.dto.PlayerRequest;
import backend.src.player.dto.PlayerSummary;
import backend.src.player.dto.PlayerUpdateRequest;
import backend.src.security.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PlayerController {

    private final PlayerService playerService;
    private final CurrentUserService currentUserService;

    public PlayerController(PlayerService playerService, CurrentUserService currentUserService) {
        this.playerService = playerService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/teams/{teamId}/players")
    public ResponseEntity<List<PlayerSummary>> getTeamPlayers(@PathVariable Integer teamId) {
        return ResponseEntity.ok(playerService.listByTeam(teamId));
    }

    @PostMapping("/teams/{teamId}/players")
    public ResponseEntity<PlayerSummary> createPlayer(@PathVariable Integer teamId,
                                                      @Valid @RequestBody PlayerRequest request) {
        Manager manager = currentUserService.getCurrentManager();
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.create(teamId, request, manager));
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<PlayerSummary> getPlayer(@PathVariable Integer id) {
        return ResponseEntity.ok(playerService.findById(id));
    }

    @PutMapping("/players/{id}")
    public ResponseEntity<PlayerSummary> updatePlayer(@PathVariable Integer id,
                                                      @Valid @RequestBody PlayerUpdateRequest request) {
        Manager manager = currentUserService.getCurrentManager();
        return ResponseEntity.ok(playerService.update(id, request, manager));
    }

    @DeleteMapping("/players/{id}")
    public ResponseEntity<Void> softDeletePlayer(@PathVariable Integer id) {
        Manager manager = currentUserService.getCurrentManager();
        playerService.softDelete(id, manager);
        return ResponseEntity.noContent().build();
    }
}
