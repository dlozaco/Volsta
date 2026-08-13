package backend.src.team;

import backend.src.exceptions.BusinessException;
import backend.src.exceptions.ResourceNotFoundException;
import backend.src.manager.Manager;
import backend.src.manager.ManagerRepository;
import backend.src.player.PositionType;
import backend.src.team.dto.TeamRequest;
import backend.src.team.dto.TeamResponse;
import backend.src.team.dto.TeamStatsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TeamServiceTest {

    private final TeamService teamService;
    private final ManagerRepository managerRepository;

    @Autowired
    public TeamServiceTest(TeamService teamService, ManagerRepository managerRepository) {
        this.teamService = teamService;
        this.managerRepository = managerRepository;
    }

    static Pageable pageable = PageRequest.of(0, 10);

    @Test
    @Transactional
    void shouldFindAllTeams() {
        assertEquals(2, teamService.findAll(pageable).getContent().size());
    }

    @Test
    @Transactional
    void shouldFindTeamById_RightId() {
        TeamResponse team = teamService.findById(1);
        assertEquals("QuokkaCV", team.name());
    }

    @Test
    @Transactional
    void shouldFindTeamById_WrongId_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> teamService.findById(-1));
    }

    @Test
    @Transactional
    void shouldFindTeamByName_RightName() {
        TeamResponse team = teamService.findByName("Barcelona");
        assertEquals(2, team.id());
    }

    @Test
    @Transactional
    void shouldFindTeamByName_WrongName_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> teamService.findByName("NonExistent"));
    }

    @Test
    @Transactional
    void shouldFindTeamsByOwner() {
        List<TeamResponse> teams = teamService.findByOwnerId(1);
        assertEquals(1, teams.size());
        assertEquals("QuokkaCV", teams.get(0).name());
    }

    @Test
    @Transactional
    void shouldCreateTeam_AsFreshManager_ReturnsOk() {
        Manager manager = newManager("new.manager@test.com", "999999998");
        TeamRequest request = new TeamRequest("NewTeam", LocalDate.of(2024, 1, 1), null);

        TeamResponse created = teamService.create(request, manager);

        assertNotNull(created.id());
        assertEquals("NewTeam", created.name());
        assertEquals("New Manager", created.ownerName());
    }

    @Test
    @Transactional
    void shouldCreateSecondTeam_WhenManagerAlreadyOwnsOne() {
        Manager owner = managerRepository.findById(1).orElseThrow();
        TeamRequest request = new TeamRequest("SecondTeam", LocalDate.of(2024, 1, 1), null);

        TeamResponse created = teamService.create(request, owner);

        assertNotNull(created.id());
        assertEquals("SecondTeam", created.name());
        assertEquals(2, teamService.findByOwnerId(1).size());
    }

    @Test
    @Transactional
    void shouldUpdateTeam_ByOwner_ReturnsOk() {
        Manager owner = managerRepository.findById(1).orElseThrow();
        TeamRequest request = new TeamRequest("UpdatedName", LocalDate.of(2024, 2, 2), null);

        TeamResponse updated = teamService.update(request, 1, owner);

        assertEquals("UpdatedName", updated.name());
        assertEquals("UpdatedName", teamService.findById(1).name());
    }

    @Test
    @Transactional
    void shouldRejectUpdateTeam_ByNonOwner() {
        Manager other = managerRepository.findById(2).orElseThrow();
        TeamRequest request = new TeamRequest("HackedName", LocalDate.of(2024, 2, 2), null);

        assertThrows(AccessDeniedException.class, () -> teamService.update(request, 1, other));
    }

    @Test
    @Transactional
    void shouldDeleteTeam_ByOwner_ReturnsVoid() {
        int count = teamService.findAll(pageable).getContent().size();

        Manager manager = newManager("delete.manager@test.com", "999999997");
        TeamRequest request = new TeamRequest("TempTeam", LocalDate.of(2024, 6, 15), null);
        TeamResponse created = teamService.create(request, manager);

        assertEquals(count + 1, teamService.findAll(pageable).getContent().size());

        teamService.delete(created.id(), manager);

        assertEquals(count, teamService.findAll(pageable).getContent().size());
    }

    @Test
    @Transactional
    void shouldRejectDeleteTeam_ThatHasMatches() {
        Manager owner = managerRepository.findById(1).orElseThrow();

        assertThrows(BusinessException.class, () -> teamService.delete(1, owner));
    }

    @Test
    @Transactional
    void shouldRejectDeleteTeam_ByNonOwner() {
        Manager other = managerRepository.findById(2).orElseThrow();

        assertThrows(AccessDeniedException.class, () -> teamService.delete(1, other));
    }

    @Test
    @Transactional
    void shouldComputeTeamStats() {
        TeamStatsResponse stats = teamService.stats(1);

        assertEquals(1, stats.teamId());
        assertEquals("QuokkaCV", stats.teamName());
        assertEquals(4, stats.matchesPlayed());
        assertEquals(3, stats.matchesWon());
        assertEquals(1, stats.matchesLost());
        assertEquals(6, stats.players().size());
        assertNotNull(stats.topScorer());
        assertEquals(3, stats.topScorer().playerId());
        assertEquals(84, stats.topScorer().totalPoints());
    }

    @Test
    @Transactional
    void shouldComputeTeamStats_PerPlayerTable() {
        TeamStatsResponse stats = teamService.stats(1);

        backend.src.match.dto.PlayerStats david = stats.players().stream()
                .filter(p -> p.playerId().equals(1))
                .findFirst()
                .orElseThrow();
        assertEquals(12, david.setsPlayed());
        assertEquals(82, david.totalPoints());
        assertEquals(21, david.totalFaults());
    }

    @Test
    @Transactional
    void shouldComputeTeamStats_TopScorerByPosition() {
        TeamStatsResponse stats = teamService.stats(1);

        assertEquals(3, stats.topScorerByPosition().get(PositionType.OPPOSITE).playerId());
        assertEquals(1, stats.topScorerByPosition().get(PositionType.MIDDLE_BLOCKER).playerId());
    }

    @Test
    @Transactional
    void shouldComputeTeamStats_FreshTeamWithoutMatches() {
        Manager manager = newManager("stats.manager@test.com", "999999996");
        TeamRequest request = new TeamRequest("NoMatchesTeam", LocalDate.of(2024, 1, 1), null);
        TeamResponse created = teamService.create(request, manager);

        TeamStatsResponse stats = teamService.stats(created.id());

        assertEquals(0, stats.matchesPlayed());
        assertEquals(0, stats.matchesWon());
        assertEquals(0, stats.matchesLost());
        assertNull(stats.topScorer());
        assertTrue(stats.players().isEmpty());
    }

    private Manager newManager(String email, String phone) {
        Manager manager = new Manager();
        manager.setName("New");
        manager.setSurname("Manager");
        manager.setEmail(email);
        manager.setPhoneNumber(phone);
        return managerRepository.save(manager);
    }
}
