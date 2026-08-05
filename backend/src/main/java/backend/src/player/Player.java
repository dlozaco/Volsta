package backend.src.player;

import backend.src.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Player extends BaseEntity {

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @NotBlank
    private String email;

    @NotNull
    private int dorsal;

    @NotNull
    private PositionType corePosition;

    /**
     * Soft delete flag (US-PLAY-03). When a player is deactivated their
     * historical statistics are kept but they disappear from the active roster.
     */
    private boolean active = true;

}
