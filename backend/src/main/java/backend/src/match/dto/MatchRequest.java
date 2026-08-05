package backend.src.match.dto;

import backend.src.match.MatchType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record MatchRequest(

        @NotNull(message = "Local team is required")
        Integer localTeamId,

        @NotNull(message = "Visitor team is required")
        Integer visitorTeamId,

        @NotNull(message = "Start moment is required")
        LocalDateTime startMoment,

        String place,

        @NotNull(message = "Match type is required")
        MatchType matchType
) {
}
