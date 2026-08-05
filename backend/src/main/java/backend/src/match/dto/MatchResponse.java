package backend.src.match.dto;

import backend.src.match.Match;
import backend.src.match.MatchType;

import java.time.LocalDateTime;
import java.util.List;

public record MatchResponse(
        Integer id,
        TeamSummary localTeam,
        TeamSummary visitorTeam,
        LocalDateTime startMoment,
        LocalDateTime endMoment,
        String place,
        MatchType matchType,
        boolean played,
        int localScore,
        int visitorScore,
        List<SetSummary> sets,
        List<NoteSummary> notes
) {
    public static MatchResponse from(Match match) {
        List<Integer> score = match.getScore();
        List<SetSummary> sets = match.getSets() == null
                ? List.of()
                : match.getSets().stream().map(SetSummary::from).toList();
        List<NoteSummary> notes = match.getNotes() == null
                ? List.of()
                : match.getNotes().stream().map(NoteSummary::from).toList();

        return new MatchResponse(
                match.getId(),
                new TeamSummary(match.getLocalTeam().getId(), match.getLocalTeam().getName(), match.getLocalTeam().getLogoUrl()),
                new TeamSummary(match.getVisitorTeam().getId(), match.getVisitorTeam().getName(), match.getVisitorTeam().getLogoUrl()),
                match.getStartMoment(),
                match.getEndMoment(),
                match.getPlace(),
                match.getMatchType(),
                !sets.isEmpty(),
                score.get(0),
                score.get(1),
                sets,
                notes
        );
    }
}
