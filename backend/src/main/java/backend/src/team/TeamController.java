package backend.src.team;

import backend.src.manager.Manager;
import backend.src.security.CurrentUserService;
import backend.src.team.dto.TeamRequest;
import backend.src.team.dto.TeamResponse;
import backend.src.team.dto.TeamStatsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final TeamService teamService;
    private final CurrentUserService currentUserService;

    @Autowired
    public TeamController(TeamService teamService, CurrentUserService currentUserService) {
        this.teamService = teamService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public ResponseEntity<Page<TeamResponse>> getAllTeams(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(teamService.findAll(pageable));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TeamResponse>> getMyTeams() {
        Manager manager = currentUserService.getCurrentManager();
        return ResponseEntity.ok(teamService.findByOwnerId(manager.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable Integer id) {
        return ResponseEntity.ok(teamService.findById(id));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<TeamStatsResponse> getTeamStats(@PathVariable Integer id) {
        return ResponseEntity.ok(teamService.stats(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TeamResponse> getTeamByName(@PathVariable String name) {
        return ResponseEntity.ok(teamService.findByName(name));
    }

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamRequest request) {
        Manager manager = currentUserService.getCurrentManager();
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.create(request, manager));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam(@PathVariable Integer id, @Valid @RequestBody TeamRequest request) {
        Manager manager = currentUserService.getCurrentManager();
        return ResponseEntity.ok(teamService.update(request, id, manager));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Integer id) {
        Manager manager = currentUserService.getCurrentManager();
        teamService.delete(id, manager);
        return ResponseEntity.noContent().build();
    }
}
