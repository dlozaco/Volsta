package backend.src.team;

import backend.src.exceptions.BusinessException;
import backend.src.exceptions.ResourceNotFoundException;
import backend.src.manager.Manager;
import backend.src.match.Match;
import backend.src.match.MatchRepository;
import backend.src.match.MatchSet;
import backend.src.match.SetParticipation;
import backend.src.match.dto.PlayerStats;
import backend.src.player.Player;
import backend.src.player.PositionType;
import backend.src.team.dto.PlayerScorer;
import backend.src.team.dto.TeamRequest;
import backend.src.team.dto.TeamResponse;
import backend.src.team.dto.TeamStatsResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        return teamRepository.findAll(pageable).map(team -> TeamResponse.from(team, matchCount(team)));
    }

    @Transactional(readOnly = true)
    public TeamResponse findById(Integer id) {
        Team team = getTeam(id);
        return TeamResponse.from(team, matchCount(team));
    }

    @Transactional(readOnly = true)
    public TeamResponse findByName(String name) {
        return teamRepository.findByName(name)
                .map(team -> TeamResponse.from(team, matchCount(team)))
                .orElseThrow(() -> new ResourceNotFoundException("Team", "name", name));
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> findByOwnerId(Integer managerId) {
        return teamRepository.findByOwnerIdOrderByIdAsc(managerId).stream()
                .map(team -> TeamResponse.from(team, matchCount(team)))
                .toList();
    }

    /**
     * Aggregated statistics for a team (US-STAT-03): match summary, top scorer,
     * top scorer per position and a detailed per-player table.
     */
    @Transactional(readOnly = true)
    public TeamStatsResponse stats(Integer teamId) {
        Team team = getTeam(teamId);
        List<Match> matches = matchRepository.findByLocalTeamIdOrVisitorTeamId(teamId, teamId);

        Set<Integer> rosterIds = team.getPlayers() == null
                ? Set.of()
                : team.getPlayers().stream().map(Player::getId).collect(Collectors.toSet());

        Map<Integer, TeamPlayerAccumulator> accumulators = new LinkedHashMap<>();
        int matchesPlayed = 0;
        int matchesWon = 0;

        for (Match match : matches) {
            if (match.getSets() == null || match.getSets().isEmpty()) {
                continue;
            }
            matchesPlayed++;
            boolean local = match.getLocalTeam() != null && match.getLocalTeam().getId().equals(teamId);
            List<Integer> score = match.getScore();
            int teamScore = local ? score.get(0) : score.get(1);
            int rivalScore = local ? score.get(1) : score.get(0);
            if (teamScore > rivalScore) {
                matchesWon++;
            }

            for (MatchSet set : match.getSets()) {
                if (set.getSetParticipations() == null) {
                    continue;
                }
                for (SetParticipation p : set.getSetParticipations()) {
                    if (!rosterIds.contains(p.getPlayer().getId())) {
                        continue;
                    }
                    accumulators.computeIfAbsent(p.getPlayer().getId(), id -> new TeamPlayerAccumulator(p.getPlayer()))
                            .accept(p);
                }
            }
        }

        List<PlayerStats> players = accumulators.values().stream()
                .map(TeamPlayerAccumulator::build)
                .toList();

        List<PlayerScorer> scorers = players.stream()
                .map(PlayerScorer::from)
                .toList();

        PlayerScorer topScorer = scorers.stream()
                .max(Comparator.comparingInt(PlayerScorer::totalPoints))
                .orElse(null);

        Map<PositionType, PlayerScorer> topScorerByPosition = new EnumMap<>(PositionType.class);
        scorers.stream()
                .sorted(Comparator.comparingInt(PlayerScorer::totalPoints).reversed())
                .forEach(scorer -> topScorerByPosition.putIfAbsent(scorer.position(), scorer));

        return new TeamStatsResponse(
                team.getId(),
                team.getName(),
                matchesPlayed,
                matchesWon,
                matchesPlayed - matchesWon,
                topScorer,
                topScorerByPosition,
                players
        );
    }

    @Transactional
    public TeamResponse create(@Valid TeamRequest request, Manager owner) {
        Team team = new Team();
        team.setName(request.name());
        team.setFoundationDate(request.foundationDate());
        team.setLogoUrl(request.logoUrl());
        team.setOwner(owner);
        return TeamResponse.from(teamRepository.save(team), 0);
    }

    @Transactional
    public TeamResponse update(@Valid TeamRequest request, Integer id, Manager manager) {
        Team team = getTeam(id);
        assertOwnership(team, manager);
        team.setName(request.name());
        team.setFoundationDate(request.foundationDate());
        team.setLogoUrl(request.logoUrl());
        return TeamResponse.from(teamRepository.save(team), matchCount(team));
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

    private long matchCount(Team team) {
        return matchRepository.countByLocalTeamIdOrVisitorTeamId(team.getId(), team.getId());
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

    private static class TeamPlayerAccumulator {
        private final Player player;
        private int setsPlayed;
        private int totalPoints;
        private int totalFaults;
        private final Set<Integer> uniqueSets = new HashSet<>();
        private final Map<PositionType, Boolean> positions = new LinkedHashMap<>();

        TeamPlayerAccumulator(Player player) {
            this.player = player;
        }

        void accept(SetParticipation participation) {
            positions.putIfAbsent(participation.getPositionType(), true);
            totalPoints += participation.getPoints();
            totalFaults += participation.getFaults();
            if (uniqueSets.add(participation.getMatchSet().getId())) {
                setsPlayed++;
            }
        }

        PlayerStats build() {
            double average = setsPlayed == 0 ? 0 : (double) totalPoints / setsPlayed;
            double denominator = totalPoints + totalFaults;
            double effectiveness = denominator == 0 ? 0 : totalPoints / denominator * 100;
            return new PlayerStats(
                    player.getId(),
                    player.getName(),
                    player.getSurname(),
                    player.getDorsal(),
                    player.getCorePosition(),
                    setsPlayed,
                    totalPoints,
                    totalFaults,
                    average,
                    effectiveness,
                    List.copyOf(positions.keySet())
            );
        }
    }
}
