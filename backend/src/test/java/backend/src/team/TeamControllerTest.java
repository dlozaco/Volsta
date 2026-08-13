package backend.src.team;

import backend.src.configuration.jwt.CustomUserDetailsService;
import backend.src.configuration.jwt.JwtUtil;
import backend.src.configuration.jwt.TokenBlacklistService;
import backend.src.exceptions.ResourceNotFoundException;
import backend.src.manager.Manager;
import backend.src.match.dto.PlayerStats;
import backend.src.player.PositionType;
import backend.src.security.CurrentUserService;
import backend.src.team.dto.PlayerScorer;
import backend.src.team.dto.TeamRequest;
import backend.src.team.dto.TeamResponse;
import backend.src.team.dto.TeamStatsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

@WebMvcTest(TeamController.class)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeamService teamService;

    @MockitoBean
    private CurrentUserService currentUserService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;


    @Test
    @WithMockUser
    void getAllTeams_ReturnsTeamPage() throws Exception {
        TeamResponse team1 = new TeamResponse(1, "Real Madrid", null, null, null, List.of(), 0);
        TeamResponse team2 = new TeamResponse(2, "Barcelona", null, null, null, List.of(), 0);

        List<TeamResponse> teams = List.of(team1, team2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<TeamResponse> teamPage = new PageImpl<>(teams, pageable, teams.size());

        when(teamService.findAll(any(Pageable.class))).thenReturn(teamPage);

        mockMvc.perform(get("/api/v1/teams")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Real Madrid"))
                .andExpect(jsonPath("$.content[1].name").value("Barcelona"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser
    void getTeamById_ExistsId_shouldReturnTeam() throws Exception {
        TeamResponse team = new TeamResponse(1, "Arsenal", null, null, null, List.of(), 0);

        when(teamService.findById(1)).thenReturn(team);

        mockMvc.perform(get("/api/v1/teams/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Arsenal"));
    }

    @Test
    @WithMockUser
    void getTeamById_NotExistingId_shouldReturnsNotFound() throws Exception {
        when(teamService.findById(99)).thenThrow(new ResourceNotFoundException("Team", "id", 99));

        mockMvc.perform(get("/api/v1/teams/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getTeamByName_ExistsTeam_shouldReturnsTeam() throws Exception {
        TeamResponse team = new TeamResponse(3, "Liverpool", null, null, null, List.of(), 0);

        when(teamService.findByName("Liverpool")).thenReturn(team);

        mockMvc.perform(get("/api/v1/teams/name/Liverpool"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Liverpool"));
    }

    @Test
    @WithMockUser
    void getTeamByName_NotExistingTeam_shouldReturnNotFound() throws Exception {
        when(teamService.findByName("Desconocido")).thenThrow(new ResourceNotFoundException("Team", "name", "Desconocido"));

        mockMvc.perform(get("/api/v1/teams/name/Desconocido"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getTeamStats_ExistsId_shouldReturnTeamStats() throws Exception {
        PlayerScorer scorer = new PlayerScorer(1, "David", "Lozano", 10, PositionType.SETTER, 21);
        PlayerStats player = new PlayerStats(
                1, "David", "Lozano", 10, PositionType.SETTER, 3, 21, 5, 7.0, 80.8, List.of(PositionType.SETTER));
        TeamStatsResponse stats = new TeamStatsResponse(
                1, "QuokkaCV", 4, 3, 1,
                scorer,
                Map.of(PositionType.SETTER, scorer),
                List.of(player)
        );

        when(teamService.stats(1)).thenReturn(stats);

        mockMvc.perform(get("/api/v1/teams/1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamId").value(1))
                .andExpect(jsonPath("$.teamName").value("QuokkaCV"))
                .andExpect(jsonPath("$.matchesPlayed").value(4))
                .andExpect(jsonPath("$.matchesWon").value(3))
                .andExpect(jsonPath("$.matchesLost").value(1))
                .andExpect(jsonPath("$.topScorer.playerName").value("David"))
                .andExpect(jsonPath("$.topScorer.totalPoints").value(21))
                .andExpect(jsonPath("$.topScorerByPosition.SETTER.playerId").value(1))
                .andExpect(jsonPath("$.players.size()").value(1))
                .andExpect(jsonPath("$.players[0].totalPoints").value(21));
    }

    @Test
    @WithMockUser
    void getTeamStats_NotExistingId_shouldReturnNotFound() throws Exception {
        when(teamService.stats(99)).thenThrow(new ResourceNotFoundException("Team", "id", 99));

        mockMvc.perform(get("/api/v1/teams/99/stats"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getMyTeams_ShouldReturnOwnerTeams() throws Exception {
        Manager manager = newManager(5);
        TeamResponse team = new TeamResponse(1, "QuokkaCV", null, null, null, List.of(), 0);

        when(currentUserService.getCurrentManager()).thenReturn(manager);
        when(teamService.findByOwnerId(5)).thenReturn(List.of(team));

        mockMvc.perform(get("/api/v1/teams/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("QuokkaCV"));
    }

    @Test
    @WithMockUser
    void createTeam_ValidRequest_shouldReturnCreated() throws Exception {
        Manager manager = newManager(5);
        TeamResponse team = new TeamResponse(10, "NewTeam", null, null, "New Manager", List.of(), 0);
        String body = """
                {
                  "name": "NewTeam",
                  "foundationDate": "2024-01-01",
                  "logoUrl": null
                }
                """;

        when(currentUserService.getCurrentManager()).thenReturn(manager);
        when(teamService.create(any(TeamRequest.class), any(Manager.class))).thenReturn(team);

        mockMvc.perform(post("/api/v1/teams")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("NewTeam"))
                .andExpect(jsonPath("$.ownerName").value("New Manager"));
    }

    @Test
    @WithMockUser
    void createTeam_MissingName_shouldReturnBadRequest() throws Exception {
        String body = """
                {
                  "foundationDate": "2024-01-01"
                }
                """;

        mockMvc.perform(post("/api/v1/teams")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateTeam_ValidRequest_shouldReturnUpdatedTeam() throws Exception {
        Manager manager = newManager(5);
        TeamResponse updated = new TeamResponse(1, "Renamed", null, null, "New Manager", List.of(), 3);
        String body = """
                {
                  "name": "Renamed",
                  "foundationDate": "2024-02-02",
                  "logoUrl": null
                }
                """;

        when(currentUserService.getCurrentManager()).thenReturn(manager);
        when(teamService.update(any(TeamRequest.class), eq(1), any(Manager.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/teams/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Renamed"))
                .andExpect(jsonPath("$.matchesCount").value(3));
    }

    @Test
    @WithMockUser
    void deleteTeam_ExistingId_shouldReturnNoContent() throws Exception {
        Manager manager = newManager(5);

        when(currentUserService.getCurrentManager()).thenReturn(manager);

        mockMvc.perform(delete("/api/v1/teams/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(teamService).delete(1, manager);
    }

    private Manager newManager(Integer id) {
        Manager manager = new Manager();
        manager.setId(id);
        manager.setName("New");
        manager.setSurname("Manager");
        manager.setEmail("manager.new@test.com");
        manager.setPhoneNumber("111111111");
        return manager;
    }
}
