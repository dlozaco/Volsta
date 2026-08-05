package backend.src.match;

import backend.src.match.dto.*;
import backend.src.security.CurrentUserService;
import backend.src.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchController {

    private final MatchService matchService;
    private final CurrentUserService currentUserService;

    public MatchController(MatchService matchService, CurrentUserService currentUserService) {
        this.matchService = matchService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public ResponseEntity<List<MatchResponse>> getAllMatches() {
        return ResponseEntity.ok(matchService.findAll());
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<MatchResponse>> getMatchesByTeam(@PathVariable Integer teamId) {
        return ResponseEntity.ok(matchService.findByTeamId(teamId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<MatchResponse>> getMatchesByType(@PathVariable MatchType type) {
        return ResponseEntity.ok(matchService.findByMatchType(type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getMatchById(@PathVariable Integer id) {
        return ResponseEntity.ok(matchService.findById(id));
    }

    @GetMapping("/{id}/sets")
    public ResponseEntity<List<SetSummary>> getMatchSets(@PathVariable Integer id) {
        return ResponseEntity.ok(matchService.findSetSummaries(id));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<List<PlayerStats>> getMatchStats(@PathVariable Integer id) {
        return ResponseEntity.ok(matchService.matchStats(id));
    }

    @PostMapping
    public ResponseEntity<MatchResponse> createMatch(@Valid @RequestBody MatchRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.create(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatchResponse> rescheduleMatch(@PathVariable Integer id,
                                                         @Valid @RequestBody MatchRescheduleRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(matchService.reschedule(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Integer id) {
        User user = currentUserService.getCurrentUser();
        matchService.delete(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sets")
    public ResponseEntity<MatchResponse> addSet(@PathVariable Integer id, @Valid @RequestBody SetRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.addSet(id, request, user));
    }

    @PostMapping("/{id}/sets/{setId}/participations")
    public ResponseEntity<MatchResponse> addParticipation(@PathVariable Integer id,
                                                          @PathVariable Integer setId,
                                                          @Valid @RequestBody SetParticipationRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.addParticipation(id, setId, request, user));
    }
}
