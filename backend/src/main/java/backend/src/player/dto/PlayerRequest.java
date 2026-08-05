package backend.src.player.dto;

import backend.src.player.Player;
import backend.src.player.PositionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PlayerRequest(

        @NotBlank(message = "Player name is required")
        @Size(max = 128)
        String name,

        @NotBlank(message = "Player surname is required")
        @Size(max = 128)
        String surname,

        @NotBlank(message = "Player email is required")
        @Email(message = "A valid email is required")
        @Size(max = 256)
        String email,

        @NotNull(message = "Dorsal is required")
        @Positive(message = "Dorsal must be a positive number")
        int dorsal,

        @NotNull(message = "Core position is required")
        PositionType corePosition
) {
}
