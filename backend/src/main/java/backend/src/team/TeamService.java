package backend.src.team;

import backend.src.exceptions.BusinessException;
import backend.src.exceptions.ResourceNotFoundException;
import backend.src.manager.Manager;
import backend.src.match.MatchRepository;
import backend.src.team.dto.TeamRequest;
import backend.src.team.dto.TeamResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class TeamService {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;

    public TeamService(TeamRepository teamRepository, MatchRepository matchRepository) {
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
    }

    @Transactional(readOnly = true)
    public Page<TeamResponse> findAll(Pageable pageable) {
        return teamRepository.findAll(pageable).map(TeamResponse::from);
    }

    @Transactional(readOnly = true)
    public TeamResponse findById(Integer id) {
        return TeamResponse.from(getTeam(id));
    }

    @Transactional(readOnly = true)
    public TeamResponse findByName(String name) {
        return teamRepository.findByName(name)
                .map(TeamResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "name", name));
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> findByOwnerId(Integer managerId) {
        return teamRepository.findByOwnerIdOrderByIdAsc(managerId).stream()
                .map(TeamResponse::from)
                .toList();
    }

    @Transactional
    public TeamResponse create(@Valid TeamRequest request, Manager owner) {
        Team team = new Team();
        team.setName(request.name());
        team.setFoundationDate(request.foundationDate());
        team.setLogoUrl(request.logoUrl());
        team.setOwner(owner);
        return TeamResponse.from(teamRepository.save(team));
    }

    @Transactional
    public TeamResponse update(@Valid TeamRequest request, Integer id, Manager manager) {
        Team team = getTeam(id);
        assertOwnership(team, manager);
        team.setName(request.name());
        team.setFoundationDate(request.foundationDate());
        team.setLogoUrl(request.logoUrl());
        return TeamResponse.from(teamRepository.save(team));
    }

    @Transactional
    public void delete(Integer id, Manager manager) {
        Team team = getTeam(id);
        assertOwnership(team, manager);
        if (matchRepository.existsByLocalTeamIdOrVisitorTeamId(id, id)) {
            throw new BusinessException("Cannot delete a team that still has matches");
        }
        teamRepository.delete(team);
    }

    private void assertOwnership(Team team, Manager manager) {
        if (team.getOwner() == null || !team.getOwner().getId().equals(manager.getId())) {
            throw new AccessDeniedException("You are not the owner of this team");
        }
    }

    private Team getTeam(Integer id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));
    }
}
