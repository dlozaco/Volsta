package backend.src.match;

import backend.src.exceptions.BusinessException;
import backend.src.exceptions.ResourceNotFoundException;
import backend.src.match.dto.*;
import backend.src.notes.dto.NotesRequest;
import backend.src.player.PositionType;
import backend.src.team.TeamService;
import backend.src.user.User;
import backend.src.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MatchServiceTest {

    private final MatchService matchService;
    private final TeamService teamService;
    private final UserRepository userRepository;

    @Autowired
    public MatchServiceTest(MatchService matchService, TeamService teamService, UserRepository userRepository) {
        this.matchService = matchService;
        this.teamService = teamService;
        this.userRepository = userRepository;
    }

    private User admin() {
        return userRepository.findUserById(1).orElseThrow();
    }

    private User manager1() {
        return userRepository.findUserById(3).orElseThrow();
    }

    private MatchRequest friendlyRequest() {
        return new MatchRequest(1, 2, LocalDateTime.now(), "Pabellón Test", MatchType.FRIENDLY);
    }

    @Test
    @Transactional
    void shouldFindAllMatches() {
        assertEquals(4, matchService.findAll().size());
    }

    @Test
    @Transactional
    void shouldFindMatchById_RightId() {
        MatchResponse match = matchService.findById(1);
        assertEquals(1, match.localTeam().id());
    }

    @Test
    @Transactional
    void shouldFindMatchById_WrongId_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> matchService.findById(-1));
    }

    @Test
    @Transactional
    void shouldFindByMatchType() {
        assertEquals(4, matchService.findByMatchType(MatchType.FRIENDLY).size());
    }

    @Test
    @Transactional
    void shouldFindByTeamId() {
        assertEquals(4, matchService.findByTeamId(1).size());
    }

    @Test
    @Transactional
    void shouldFindMatchSetsById() {
        assertEquals(3, matchService.findMatchSetsById(1).size());
    }

    @Test
    @Transactional
    void shouldCreateFriendlyMatch_ByManager_ReturnsOk() {
        int count = matchService.findAll().size();

        MatchResponse created = matchService.create(friendlyRequest(), manager1());

        assertNotNull(created.id());
        assertEquals(count + 1, matchService.findAll().size());
        assertFalse(created.played());
    }

    @Test
    @Transactional
    void shouldCreateLeagueMatch_ByAdmin_ReturnsOk() {
        MatchRequest request = new MatchRequest(1, 2, LocalDateTime.now(), "Liga", MatchType.LEAGUE);

        MatchResponse created = matchService.create(request, admin());

        assertNotNull(created.id());
        assertEquals(MatchType.LEAGUE, created.matchType());
    }

    @Test
    @Transactional
    void shouldRejectLeagueMatch_ByManager() {
        MatchRequest request = new MatchRequest(1, 2, LocalDateTime.now(), "Liga", MatchType.LEAGUE);

        assertThrows(AccessDeniedException.class, () -> matchService.create(request, manager1()));
    }

    @Test
    @Transactional
    void shouldCreateFriendlyMatch_ByAdmin() {
        MatchResponse created = matchService.create(friendlyRequest(), admin());

        assertNotNull(created.id());
    }

    @Test
    @Transactional
    void shouldRejectMatch_AgainstItself() {
        MatchRequest request = new MatchRequest(1, 1, LocalDateTime.now(), "Same", MatchType.FRIENDLY);

        assertThrows(BusinessException.class, () -> matchService.create(request, manager1()));
    }

    @Test
    @Transactional
    void shouldReschedule_NotPlayedMatch_ByOwner() {
        MatchResponse created = matchService.create(friendlyRequest(), manager1());
        LocalDateTime newMoment = LocalDateTime.of(2026, 12, 1, 18, 0);
        MatchRescheduleRequest reschedule = new MatchRescheduleRequest(newMoment, "Nuevo Pabellón");

        MatchResponse updated = matchService.reschedule(created.id(), reschedule, manager1());

        assertEquals(newMoment, updated.startMoment());
        assertEquals("Nuevo Pabellón", updated.place());
    }

    @Test
    @Transactional
    void shouldRejectReschedule_PlayedMatch() {
        MatchRescheduleRequest reschedule = new MatchRescheduleRequest(LocalDateTime.now(), "X");

        assertThrows(BusinessException.class, () -> matchService.reschedule(1, reschedule, manager1()));
    }

    @Test
    @Transactional
    void shouldDeleteMatch_ByOwner_ReturnsVoid() {
        int count = matchService.findAll().size();
        MatchResponse created = matchService.create(friendlyRequest(), manager1());

        assertEquals(count + 1, matchService.findAll().size());
        matchService.delete(created.id(), manager1());
        assertEquals(count, matchService.findAll().size());
    }

    @Test
    @Transactional
    void shouldAddSet_AndMarkMatchAsPlayed() {
        MatchResponse created = matchService.create(friendlyRequest(), manager1());
        SetRequest setRequest = new SetRequest(1, 25, 20);

        MatchResponse updated = matchService.addSet(created.id(), setRequest, manager1());

        assertTrue(updated.played());
        assertEquals(1, updated.localScore());
    }

    @Test
    @Transactional
    void shouldAddParticipation_ForPlayerInMatch() {
        MatchResponse created = matchService.create(friendlyRequest(), manager1());
        MatchResponse withSet = matchService.addSet(created.id(), new SetRequest(1, 25, 20), manager1());
        Integer setId = withSet.sets().get(0).id();

        SetParticipationRequest participation = new SetParticipationRequest(1, PositionType.SETTER, 7, 2);

        matchService.addParticipation(created.id(), setId, participation, manager1());
        List<SetSummary> sets = matchService.findSetSummaries(created.id());

        assertEquals(1, sets.get(0).participations().size());
        assertEquals(7, sets.get(0).participations().get(0).points());
        assertEquals(2, sets.get(0).participations().get(0).faults());
    }

    @Test
    @Transactional
    void shouldCreateNote_ForPlayerInMatch() {
        MatchResponse created = matchService.create(friendlyRequest(), manager1());
        NotesRequest noteRequest = new NotesRequest(1, "Buen saque", "Muy consistente");

        NoteSummary note = matchService.createNote(created.id(), noteRequest, manager1());

        assertNotNull(note.id());
        assertEquals("Buen saque", note.subject());
    }

    @Test
    @Transactional
    void shouldDeleteNote_ByOwner_LeavesMatchIntact() {
        MatchResponse created = matchService.create(friendlyRequest(), manager1());
        NoteSummary note = matchService.createNote(created.id(),
                new NotesRequest(1, "Buen saque", "Muy consistente"), manager1());

        matchService.deleteNote(created.id(), note.id(), manager1());

        MatchResponse match = matchService.findById(created.id());
        assertTrue(match.notes().isEmpty());
    }

    @Test
    @Transactional
    void shouldComputeMatchStats() {
        List<PlayerStats> stats = matchService.matchStats(1);

        PlayerStats david = stats.stream().filter(s -> s.playerId().equals(1)).findFirst().orElseThrow();
        assertEquals(3, david.setsPlayed());
        assertEquals(21, david.totalPoints());
        assertEquals(5, david.totalFaults());
        assertEquals(21.0 / 26.0 * 100, david.effectiveness(), 0.001);
    }
}
