package backend.src.match;

import backend.src.exceptions.ResourceNotFoundException;
import backend.src.team.Team;
import backend.src.team.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MatchServiceTest {

    private final MatchService matchService;
    private final TeamService teamService;

    @Autowired
    public MatchServiceTest(MatchService matchService, TeamService teamService) {
        this.matchService = matchService;
        this.teamService = teamService;
    }

    @Test
    void shouldFindAllMatches() {
        assertEquals(4, matchService.findAll().size());
    }

    @Test
    void shouldFindMatchById_RightId() {
        Match match = matchService.findById(1);
        assertEquals(1, match.getLocalTeam().getId());
    }

    @Test
    void shouldFindMatchById_WrongId_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> matchService.findById(-1));
    }

    @Test
    void shouldFindByMatchType() {
        assertEquals(4, matchService.findByMatchType(MatchType.FRIENDLY).size());
    }

    @Test
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
    void shouldFindMatchNotesById() {
        assertEquals(2, matchService.findMatchNotesById(1).size());
    }

    @Test
    @Transactional
    void shouldCreateMatch_ReturnsOk() {
        int count = matchService.findAll().size();
        Team local = teamService.findById(1);
        Team visitor = teamService.findById(2);

        Match match = new Match();
        match.setLocalTeam(local);
        match.setVisitorTeam(visitor);
        match.setStartMoment(LocalDateTime.now());
        match.setMatchType(MatchType.LEAGUE);
        matchService.create(match);

        assertNotNull(match.getId());
        assertEquals(count + 1, matchService.findAll().size());
    }

    @Test
    @Transactional
    void shouldUpdateMatch_RightId_ReturnsOk() {
        Match match = matchService.findById(1);
        match.setPlace("Updated Place");
        matchService.update(match, 1);

        Match updated = matchService.findById(1);
        assertEquals("Updated Place", updated.getPlace());
    }

    @Test
    @Transactional
    void shouldDeleteMatch_ReturnsVoid() {
        int count = matchService.findAll().size();
        Team local = teamService.findById(1);
        Team visitor = teamService.findById(2);

        Match match = new Match();
        match.setLocalTeam(local);
        match.setVisitorTeam(visitor);
        match.setStartMoment(LocalDateTime.now());
        match.setMatchType(MatchType.FRIENDLY);
        matchService.create(match);

        assertEquals(count + 1, matchService.findAll().size());
        matchService.delete(match.getId());
        assertEquals(count, matchService.findAll().size());
    }
}
