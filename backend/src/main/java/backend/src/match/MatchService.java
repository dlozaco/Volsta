package backend.src.match;

import backend.src.exceptions.BusinessException;
import backend.src.exceptions.ResourceNotFoundException;
import backend.src.manager.Manager;
import backend.src.manager.ManagerRepository;
import backend.src.match.dto.*;
import backend.src.notes.Notes;
import backend.src.notes.NotesRepository;
import backend.src.notes.dto.NotesRequest;
import backend.src.player.Player;
import backend.src.player.PlayerRepository;
import backend.src.player.PositionType;
import backend.src.team.Team;
import backend.src.team.TeamRepository;
import backend.src.user.User;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Validated
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchSetRepository matchSetRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final ManagerRepository managerRepository;
    private final NotesRepository notesRepository;

    public MatchService(MatchRepository matchRepository,
                        MatchSetRepository matchSetRepository,
                        TeamRepository teamRepository,
                        PlayerRepository playerRepository,
                        ManagerRepository managerRepository,
                        NotesRepository notesRepository) {
        this.matchRepository = matchRepository;
        this.matchSetRepository = matchSetRepository;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.managerRepository = managerRepository;
        this.notesRepository = notesRepository;
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> findAll() {
        return matchRepository.findAll().stream().map(MatchResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MatchResponse findById(Integer id) {
        return MatchResponse.from(getMatch(id));
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> findByMatchType(MatchType matchType) {
        return matchRepository.findByMatchType(matchType).stream().map(MatchResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> findByTeamId(Integer teamId) {
        return matchRepository.findByLocalTeamIdOrVisitorTeamId(teamId, teamId).stream().map(MatchResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MatchSet> findMatchSetsById(Integer matchId) {
        Match match = getMatch(matchId);
        return match.getSets() == null ? List.of() : match.getSets();
    }

    @Transactional(readOnly = true)
    public List<SetSummary> findSetSummaries(Integer matchId) {
        return findMatchSetsById(matchId).stream().map(SetSummary::from).toList();
    }

    @Transactional
    public MatchResponse create(@Valid MatchRequest request, User user) {
        Team local = getTeam(request.localTeamId());
        Team visitor = getTeam(request.visitorTeamId());

        if (local.getId().equals(visitor.getId())) {
            throw new BusinessException("A team cannot play against itself");
        }
        authorizeMatchWrite(request.matchType(), request.localTeamId(), request.visitorTeamId(), user);

        Match match = new Match();
        match.setLocalTeam(local);
        match.setVisitorTeam(visitor);
        match.setStartMoment(request.startMoment());
        match.setPlace(request.place());
        match.setMatchType(request.matchType());

        return MatchResponse.from(matchRepository.save(match));
    }

    /**
     * Reschedule a match that has not been played yet (US-MATC-02 / US-MATC-04).
     */
    @Transactional
    public MatchResponse reschedule(Integer id, @Valid MatchRescheduleRequest request, User user) {
        Match match = getMatch(id);

        if (match.getSets() != null && !match.getSets().isEmpty()) {
            throw new BusinessException("Cannot reschedule a match that has already been played");
        }
        authorizeMatchWrite(match.getMatchType(), match.getLocalTeam().getId(), match.getVisitorTeam().getId(), user);

        match.setStartMoment(request.startMoment());
        if (request.place() != null && !request.place().isBlank()) {
            match.setPlace(request.place());
        }

        return MatchResponse.from(matchRepository.save(match));
    }

    @Transactional
    public void delete(Integer id, User user) {
        Match match = getMatch(id);
        authorizeMatchWrite(match.getMatchType(), match.getLocalTeam().getId(), match.getVisitorTeam().getId(), user);
        matchRepository.delete(match);
    }

    @Transactional
    public MatchResponse addSet(Integer matchId, @Valid SetRequest request, User user) {
        Match match = getMatch(matchId);
        authorizeMatchWrite(match.getMatchType(), match.getLocalTeam().getId(), match.getVisitorTeam().getId(), user);

        if (match.getSets() == null) {
            match.setSets(new ArrayList<>());
        }

        MatchSet set = new MatchSet();
        set.setSetNumber(request.setNumber());
        set.setLocalTeamScore(request.localTeamScore());
        set.setVisitorTeamScore(request.visitorTeamScore());
        set.setSetParticipations(new ArrayList<>());

        match.getSets().add(set);
        match.setEndMoment(java.time.LocalDateTime.now());
        return MatchResponse.from(matchRepository.save(match));
    }

    @Transactional
    public MatchResponse addParticipation(Integer matchId, Integer setId, @Valid SetParticipationRequest request, User user) {
        Match match = getMatch(matchId);
        authorizeMatchWrite(match.getMatchType(), match.getLocalTeam().getId(), match.getVisitorTeam().getId(), user);

        MatchSet set = match.getSets().stream()
                .filter(s -> s.getId().equals(setId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("MatchSet", "id", setId));

        Player player = getPlayer(request.playerId());
        Team playerTeam = teamRepository.findFirstByPlayersId(player.getId())
                .orElseThrow(() -> new BusinessException("Player does not belong to any team"));

        if (!playerTeam.getId().equals(match.getLocalTeam().getId())
                && !playerTeam.getId().equals(match.getVisitorTeam().getId())) {
            throw new BusinessException("Player does not belong to any of the competing teams");
        }

        SetParticipation participation = new SetParticipation();
        participation.setPlayer(player);
        participation.setMatchSet(set);
        participation.setPositionType(request.positionType());
        participation.setPoints(request.points());
        participation.setFaults(request.faults());

        if (set.getSetParticipations() == null) {
            set.setSetParticipations(new ArrayList<>());
        }
        set.getSetParticipations().add(participation);
        matchSetRepository.save(set);

        return MatchResponse.from(match);
    }

    @Transactional
    public NoteSummary createNote(Integer matchId, @Valid NotesRequest request, User user) {
        Match match = getMatch(matchId);
        authorizeMatchWrite(match.getMatchType(), match.getLocalTeam().getId(), match.getVisitorTeam().getId(), user);

        Player player = getPlayer(request.playerId());
        Team playerTeam = teamRepository.findFirstByPlayersId(player.getId())
                .orElseThrow(() -> new BusinessException("Player does not belong to any team"));
        if (!playerTeam.getId().equals(match.getLocalTeam().getId())
                && !playerTeam.getId().equals(match.getVisitorTeam().getId())) {
            throw new BusinessException("Player does not belong to any of the competing teams");
        }

        Notes note = new Notes();
        note.setSubject(request.subject());
        note.setDescription(request.description());
        note.setPlayer(player);
        Notes saved = notesRepository.save(note);

        if (match.getNotes() == null) {
            match.setNotes(new ArrayList<>());
        }
        match.getNotes().add(saved);
        matchRepository.save(match);

        return NoteSummary.from(saved);
    }

    @Transactional
    public void deleteNote(Integer matchId, Integer noteId, User user) {
        Match match = getMatch(matchId);
        authorizeMatchWrite(match.getMatchType(), match.getLocalTeam().getId(), match.getVisitorTeam().getId(), user);

        Notes note = notesRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Notes", "id", noteId));

        if (match.getNotes() != null) {
            match.getNotes().removeIf(n -> n.getId().equals(noteId));
            matchRepository.save(match);
        }
        notesRepository.delete(note);
    }

    /**
     * Aggregated per-player statistics for a match (US-STAT-02).
     */
    @Transactional(readOnly = true)
    public List<PlayerStats> matchStats(Integer matchId) {
        Match match = getMatch(matchId);
        Map<Integer, PlayerStatsAccumulator> accumulators = new LinkedHashMap<>();

        if (match.getSets() != null) {
            for (MatchSet set : match.getSets()) {
                if (set.getSetParticipations() == null) {
                    continue;
                }
                for (SetParticipation p : set.getSetParticipations()) {
                    accumulators.computeIfAbsent(p.getPlayer().getId(), id -> new PlayerStatsAccumulator(p.getPlayer()))
                            .accept(p, set.getSetNumber());
                }
            }
        }

        return accumulators.values().stream().map(PlayerStatsAccumulator::build).toList();
    }

    /**
     * A match can be written by an ADMIN, or by a MANAGER that owns one of the
     * two competing teams. League matches are reserved to the ADMIN.
     */
    private void authorizeMatchWrite(MatchType type, Integer localTeamId, Integer visitorTeamId, User user) {
        if (user.hasAuthority("ADMIN")) {
            return;
        }
        if (type == MatchType.LEAGUE) {
            throw new AccessDeniedException("Only an ADMIN can manage league matches");
        }
        if (!user.hasAuthority("MANAGER")) {
            throw new AccessDeniedException("Only MANAGER or ADMIN can manage matches");
        }
        Manager manager = managerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager", "userId", user.getId()));

        Team local = getTeam(localTeamId);
        Team visitor = getTeam(visitorTeamId);
        boolean ownsLocal = local.getOwner() != null && local.getOwner().getId().equals(manager.getId());
        boolean ownsVisitor = visitor.getOwner() != null && visitor.getOwner().getId().equals(manager.getId());

        if (!ownsLocal && !ownsVisitor) {
            throw new AccessDeniedException("You must own one of the competing teams");
        }
    }

    private Match getMatch(Integer id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match", "id", id));
    }

    private Team getTeam(Integer id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));
    }

    private Player getPlayer(Integer id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player", "id", id));
    }

    private static class PlayerStatsAccumulator {
        private final Player player;
        private int setsPlayed;
        private int totalPoints;
        private int totalFaults;
        private final Map<PositionType, Boolean> positions = new LinkedHashMap<>();

        PlayerStatsAccumulator(Player player) {
            this.player = player;
        }

        void accept(SetParticipation participation, int setNumber) {
            setsPlayed++;
            totalPoints += participation.getPoints();
            totalFaults += participation.getFaults();
            positions.put(participation.getPositionType(), true);
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
