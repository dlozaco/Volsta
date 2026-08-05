package backend.src.match;

import backend.src.model.BaseEntity;
import backend.src.player.Player;
import backend.src.player.PositionType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SetParticipation extends BaseEntity {

    @NotNull
    private PositionType positionType;

    @PositiveOrZero
    private int points;

    @PositiveOrZero
    private int faults;

    @NotNull
    @ManyToOne
    private Player player;

    @NotNull
    @ManyToOne
    MatchSet matchSet;
}
