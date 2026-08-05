package backend.src.match;

import backend.src.model.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MatchSet extends BaseEntity {


    @NotNull
    private int setNumber;

    @NotNull
    private int localTeamScore;

    @NotNull
    private int visitorTeamScore;

    @OneToMany(mappedBy = "matchSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SetParticipation> setParticipations;
}
