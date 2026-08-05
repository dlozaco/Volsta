package backend.src.match.dto;

import backend.src.match.MatchSet;
import backend.src.match.SetParticipation;
import backend.src.player.PositionType;

public record SetParticipationSummary(
        Integer id,
        Integer playerId,
        String playerName,
        String playerSurname,
        int dorsal,
        PositionType positionType,
        int points,
        int faults
) {
    public static SetParticipationSummary from(SetParticipation participation) {
        return new SetParticipationSummary(
                participation.getId(),
                participation.getPlayer().getId(),
                participation.getPlayer().getName(),
                participation.getPlayer().getSurname(),
                participation.getPlayer().getDorsal(),
                participation.getPositionType(),
                participation.getPoints(),
                participation.getFaults()
        );
    }
}
