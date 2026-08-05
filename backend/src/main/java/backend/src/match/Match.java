package backend.src.match;

import backend.src.model.BaseEntity;
import backend.src.notes.Notes;
import backend.src.team.Team;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Match extends BaseEntity {

    @ManyToOne
    private Team localTeam;

    @ManyToOne
    private Team visitorTeam;

    @NotNull
    private LocalDateTime startMoment;

    private LocalDateTime endMoment;

    private String place;

    @NotNull
    private MatchType matchType;

    @OneToMany
    private List<Notes> notes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<MatchSet> sets;

    public List<Integer> getScore(){
        List<Integer> res = new ArrayList<>();
        res.add(0);
        res.add(0);
        if (this.sets != null) {
            for (MatchSet set : this.sets) {
                if (set.getLocalTeamScore() > set.getVisitorTeamScore()){
                    res.set(0, res.get(0) + 1);
                } else{
                    res.set(1, res.get(1) + 1);
                }
            }
        }
        return res;
    }
}
