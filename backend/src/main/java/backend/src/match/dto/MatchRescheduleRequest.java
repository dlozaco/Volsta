package backend.src.match.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Used to reschedule a match that has not been played yet (US-MATC-02 / US-MATC-04).
 */
public record MatchRescheduleRequest(

        @NotNull(message = "Start moment is required")
        LocalDateTime startMoment,

        String place
) {
}
