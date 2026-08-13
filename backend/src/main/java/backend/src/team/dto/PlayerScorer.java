package backend.src.team.dto;

import backend.src.match.dto.PlayerStats;
import backend.src.player.PositionType;

/**
 * Scorer summary for a team (US-STAT-03): overall top scorer and top scorer
 * per position.
 */
public record PlayerScorer(
        Integer playerId,
        String playerName,
        String playerSurname,
        int dorsal,
        PositionType position,
        int totalPoints
) {
    public static PlayerScorer from(PlayerStats stats) {
        return new PlayerScorer(
                stats.playerId(),
                stats.playerName(),
                stats.playerSurname(),
                stats.dorsal(),
                stats.corePosition(),
                stats.totalPoints()
        );
    }
}
