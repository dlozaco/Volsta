package backend.src.match.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SetRequest(
        @NotNull(message = "Set number is required")
        @PositiveOrZero(message = "Set number must be zero or positive")
        int setNumber,

        @NotNull(message = "Local team score is required")
        @PositiveOrZero(message = "Score must be zero or positive")
        int localTeamScore,

        @NotNull(message = "Visitor team score is required")
        @PositiveOrZero(message = "Score must be zero or positive")
        int visitorTeamScore
) {
}
