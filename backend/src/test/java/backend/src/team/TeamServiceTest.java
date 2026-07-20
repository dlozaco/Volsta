package backend.src.team;

import backend.src.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    static Pageable pageable = PageRequest.of(0, 10);

    @Test
    @Transactional
    void shouldFindAllTeams() {
        assertEquals(2, teamService.findAll(pageable).getContent().size());
    }

    @Test
    @Transactional
    void shouldFindTeamById_RightId() {
        Team team = teamService.findById(1);
        assertEquals("QuokkaCV", team.getName());
    }

    @Test
    @Transactional
    void shouldFindTeamById_WrongId_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> teamService.findById(-1));
    }

    @Test
    @Transactional
    void shouldFindTeamByName_RightName() {
        Team team = teamService.findByName("Barcelona");
        assertEquals(2, team.getId());
    }

    @Test
    @Transactional
    void shouldFindTeamByName_WrongName_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> teamService.findByName("NonExistent"));
    }

    @Test
    @Transactional
    void shouldCreateTeam_ReturnsOk() {
        int count = teamService.findAll(pageable).getContent().size();

        Team team = new Team();
        team.setName("NewTeam");
        team.setFoundationDate(LocalDate.of(2024, 1, 1));

        teamService.create(team);
        assertNotNull(team.getId());

        int newCount = teamService.findAll(pageable).getContent().size();

        assertEquals(count + 1, newCount );
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
        int count = teamService.findAll(pageable).getContent().size();

        Team team = new Team();
        team.setName("TempTeam");
        team.setFoundationDate(LocalDate.of(2024, 6, 15));
        teamService.create(team);

        assertEquals(count + 1, teamService.findAll(pageable).getContent().size());
        teamService.delete(team.getId());
        assertEquals(count, teamService.findAll(pageable).getContent().size());
    }
}
