package backend.src.team.dto;

import backend.src.match.dto.PlayerStats;
import backend.src.player.PositionType;

import java.util.List;
import java.util.Map;

/**
 * Aggregated team statistics (US-STAT-03): match summary, top scorers and a
 * detailed per-player performance table.
 */
public record TeamStatsResponse(
        Integer teamId,
        String teamName,
        int matchesPlayed,
        int matchesWon,
        int matchesLost,
        PlayerScorer topScorer,
        Map<PositionType, PlayerScorer> topScorerByPosition,
        List<PlayerStats> players
) {
}
