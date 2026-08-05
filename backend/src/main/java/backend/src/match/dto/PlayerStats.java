package backend.src.match.dto;

import backend.src.player.PositionType;

import java.util.List;

/**
 * Aggregated per-player statistics for a finished match (US-STAT-02).
 */
public record PlayerStats(
        Integer playerId,
        String playerName,
        String playerSurname,
        int dorsal,
        PositionType corePosition,
        int setsPlayed,
        int totalPoints,
        int totalFaults,
        double averagePointsPerSet,
        double effectiveness,
        List<PositionType> positionsUsed
) {
}
