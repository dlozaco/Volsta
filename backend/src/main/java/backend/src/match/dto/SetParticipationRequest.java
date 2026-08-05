package backend.src.match.dto;

import backend.src.player.PositionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SetParticipationRequest(
        @NotNull(message = "Player is required")
        Integer playerId,

        @NotNull(message = "Position is required")
        PositionType positionType,

        @NotNull(message = "Points are required")
        @PositiveOrZero(message = "Points must be zero or positive")
        int points,

        @NotNull(message = "Faults are required")
        @PositiveOrZero(message = "Faults must be zero or positive")
        int faults
) {
}
