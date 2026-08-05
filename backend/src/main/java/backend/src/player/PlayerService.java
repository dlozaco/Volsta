package backend.src.player;

import backend.src.exceptions.BusinessException;
import backend.src.exceptions.ResourceNotFoundException;
import backend.src.manager.Manager;
import backend.src.player.dto.PlayerRequest;
import backend.src.player.dto.PlayerSummary;
import backend.src.player.dto.PlayerUpdateRequest;
import backend.src.team.Team;
import backend.src.team.TeamRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public PlayerService(PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional(readOnly = true)
    public List<PlayerSummary> listByTeam(Integer teamId) {
        Team team = getTeam(teamId);
        return team.getPlayers().stream()
                .filter(Player::isActive)
                .map(PlayerSummary::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlayerSummary findById(Integer id) {
        return PlayerSummary.from(getPlayer(id));
    }

    @Transactional
    public PlayerSummary create(Integer teamId, @Valid PlayerRequest request, Manager manager) {
        Team team = getTeam(teamId);
        assertOwnership(team, manager);

        if (playerRepository.existsByEmail(request.email())) {
            throw new BusinessException("A player with this email already exists");
        }
        if (dorsalAlreadyUsed(team, request.dorsal(), null)) {
            throw new BusinessException("Dorsal already in use in this team");
        }

        Player player = new Player();
        player.setName(request.name());
        player.setSurname(request.surname());
        player.setEmail(request.email());
        player.setDorsal(request.dorsal());
        player.setCorePosition(request.corePosition());
        player.setActive(true);

        Player saved = playerRepository.save(player);
        team.getPlayers().add(saved);
        teamRepository.save(team);

        return PlayerSummary.from(saved);
    }

    @Transactional
    public PlayerSummary update(Integer id, @Valid PlayerUpdateRequest request, Manager manager) {
        Player player = getPlayer(id);
        Team team = teamOf(player.getId());
        assertOwnership(team, manager);

        if (playerRepository.existsByEmail(request.email()) && !player.getEmail().equalsIgnoreCase(request.email())) {
            throw new BusinessException("A player with this email already exists");
        }
        if (dorsalAlreadyUsed(team, request.dorsal(), player.getId())) {
            throw new BusinessException("Dorsal already in use in this team");
        }

        player.setName(request.name());
        player.setSurname(request.surname());
        player.setEmail(request.email());
        player.setDorsal(request.dorsal());
        player.setCorePosition(request.corePosition());

        return PlayerSummary.from(playerRepository.save(player));
    }

    /**
     * Soft delete (US-PLAY-03): keeps the player's history but removes them
     * from the active roster.
     */
    @Transactional
    public void softDelete(Integer id, Manager manager) {
        Player player = getPlayer(id);
        Team team = teamOf(player.getId());
        assertOwnership(team, manager);

        player.setActive(false);
        playerRepository.save(player);
    }

    private boolean dorsalAlreadyUsed(Team team, int dorsal, Integer excludePlayerId) {
        if (team.getPlayers() == null) {
            return false;
        }
        return team.getPlayers().stream()
                .filter(Player::isActive)
                .anyMatch(p -> p.getDorsal() == dorsal && !p.getId().equals(excludePlayerId));
    }

    private void assertOwnership(Team team, Manager manager) {
        if (team.getOwner() == null || !team.getOwner().getId().equals(manager.getId())) {
            throw new AccessDeniedException("You are not the owner of this team");
        }
    }

    private Team teamOf(Integer playerId) {
        return teamRepository.findFirstByPlayersId(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "playerId", playerId));
    }

    private Player getPlayer(Integer id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player", "id", id));
    }

    private Team getTeam(Integer id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));
    }
}
