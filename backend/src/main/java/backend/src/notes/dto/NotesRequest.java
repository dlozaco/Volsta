package backend.src.notes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotesRequest(
        @NotNull(message = "Player is required")
        Integer playerId,

        @NotEmpty(message = "Subject is required")
        @Size(max = 256)
        String subject,

        @NotBlank(message = "Description is required")
        @Size(max = 512)
        String description
) {
}
