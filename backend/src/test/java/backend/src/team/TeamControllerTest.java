package backend.src.team;

import backend.src.configuration.jwt.CustomUserDetailsService;
import backend.src.configuration.jwt.JwtUtil;
import backend.src.configuration.jwt.TokenBlacklistService;
import backend.src.exceptions.ResourceNotFoundException;
import backend.src.security.CurrentUserService;
import backend.src.team.dto.TeamResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
