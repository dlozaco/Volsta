package backend.src.team;

import backend.src.configuration.jwt.CustomUserDetailsService;
import backend.src.configuration.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
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
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    @WithMockUser
    void getAllTeams_ReturnsTeamPage() throws Exception {
        Team team1 = new Team();
        team1.setId(1);
        team1.setName("Real Madrid");

        Team team2 = new Team();
        team2.setId(2);
        team2.setName("Barcelona");

        List<Team> teams = Arrays.asList(team1, team2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Team> teamPage = new PageImpl<>(teams, pageable, teams.size());

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
        Team team = new Team();
        team.setId(1);
        team.setName("Arsenal");

        when(teamService.findById(1)).thenReturn(team);

        mockMvc.perform(get("/api/v1/teams/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Arsenal"));
    }

    @Test
    @WithMockUser
    void getTeamById_NotExistingId_shouldReturnsNotFound() throws Exception {
        when(teamService.findById(99)).thenReturn(null);

        mockMvc.perform(get("/api/v1/teams/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getTeamByName_ExistsTeam_shouldReturnsTeam() throws Exception {
        Team team = new Team();
        team.setId(3);
        team.setName("Liverpool");

        when(teamService.findByName("Liverpool")).thenReturn(team);

        mockMvc.perform(get("/api/v1/teams/name/Liverpool"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Liverpool"));
    }

    @Test
    @WithMockUser
    void getTeamByName_NotExistingTeam_shouldReturnTeam() throws Exception {
        when(teamService.findByName("Desconocido")).thenReturn(null);

        mockMvc.perform(get("/api/v1/teams/name/Desconocido"))
                .andExpect(status().isNotFound());
    }
}