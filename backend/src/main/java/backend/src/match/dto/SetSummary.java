package backend.src.match.dto;

import backend.src.match.MatchSet;

import java.util.List;

public record SetSummary(
        Integer id,
        int setNumber,
        int localTeamScore,
        int visitorTeamScore,
        List<SetParticipationSummary> participations
) {
    public static SetSummary from(MatchSet set) {
        List<SetParticipationSummary> participations = set.getSetParticipations() == null
                ? List.of()
                : set.getSetParticipations().stream().map(SetParticipationSummary::from).toList();
        return new SetSummary(
                set.getId(),
                set.getSetNumber(),
                set.getLocalTeamScore(),
                set.getVisitorTeamScore(),
                participations
        );
    }
}
