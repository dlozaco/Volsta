package backend.src.team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TeamRequest(

        @NotBlank(message = "Team name is required")
        @Size(max = 256)
        String name,

        @NotNull(message = "Foundation date is required")
        LocalDate foundationDate,

        @Size(max = 512)
        String logoUrl
) {
}
