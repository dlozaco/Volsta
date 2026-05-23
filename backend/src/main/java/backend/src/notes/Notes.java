package backend.src.notes;

import backend.src.model.BaseEntity;
import backend.src.player.Player;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notes extends BaseEntity {

    @NotEmpty
    @Size(max = 256)
    private String subject;

    @NotBlank
    @Size(max = 512)
    private String description;

    @NotNull
    @ManyToOne
    private Player player;
}
