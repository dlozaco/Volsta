package backend.src.notes;

import backend.src.match.MatchService;
import backend.src.match.dto.NoteSummary;
import backend.src.notes.dto.NotesRequest;
import backend.src.security.CurrentUserService;
import backend.src.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/matches/{matchId}/notes")
public class NotesController {

    private final MatchService matchService;
    private final CurrentUserService currentUserService;

    public NotesController(MatchService matchService, CurrentUserService currentUserService) {
        this.matchService = matchService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ResponseEntity<NoteSummary> createNote(@PathVariable Integer matchId,
                                                  @Valid @RequestBody NotesRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.createNote(matchId, request, user));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(@PathVariable Integer matchId, @PathVariable Integer noteId) {
        User user = currentUserService.getCurrentUser();
        matchService.deleteNote(matchId, noteId, user);
        return ResponseEntity.noContent().build();
    }
}
