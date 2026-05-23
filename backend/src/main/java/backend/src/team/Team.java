package backend.src.team;

import backend.src.match.Match;
import backend.src.model.BaseEntity;
import backend.src.player.Player;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Team extends BaseEntity {

    @NotBlank
    @Size(max = 256)
    private String name;

    @NotNull
    private LocalDate foundationDate;

    private String logoUrl;

    @OneToMany
    List<Player> players;

    @ManyToMany
    List<Match> matches;

}
