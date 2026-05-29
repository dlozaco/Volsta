package backend.src.team;

import backend.src.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TeamServiceTest {

    private final TeamService teamService;

    @Autowired
    public TeamServiceTest(TeamService teamService) {
        this.teamService = teamService;
    }

    @Test
    void shouldFindAllTeams() {
        assertEquals(2, teamService.findAll().size());
    }

    @Test
    void shouldFindTeamById_RightId() {
        Team team = teamService.findById(1);
        assertEquals("QuokkaCV", team.getName());
    }

    @Test
    void shouldFindTeamById_WrongId_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> teamService.findById(-1));
    }

    @Test
    void shouldFindTeamByName_RightName() {
        Team team = teamService.findByName("Barcelona");
        assertEquals(2, team.getId());
    }

    @Test
    void shouldFindTeamByName_WrongName_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> teamService.findByName("NonExistent"));
    }

    @Test
    @Transactional
    void shouldCreateTeam_ReturnsOk() {
        int count = teamService.findAll().size();

        Team team = new Team();
        team.setName("NewTeam");
        team.setFoundationDate(LocalDate.of(2024, 1, 1));

        teamService.create(team);
        assertNotNull(team.getId());

        assertEquals(count + 1, teamService.findAll().size());
    }

    @Test
    @Transactional
    void shouldUpdateTeam_RightId_ReturnsOk() {
        Team team = teamService.findById(1);
        team.setName("UpdatedName");
        teamService.update(team, 1);
        Team updated = teamService.findById(1);
        assertEquals("UpdatedName", updated.getName());
    }

    @Test
    @Transactional
    void shouldDeleteTeam_ReturnsVoid() {
        int count = teamService.findAll().size();

        Team team = new Team();
        team.setName("TempTeam");
        team.setFoundationDate(LocalDate.of(2024, 6, 15));
        teamService.create(team);

        assertEquals(count + 1, teamService.findAll().size());
        teamService.delete(team.getId());
        assertEquals(count, teamService.findAll().size());
    }
}
