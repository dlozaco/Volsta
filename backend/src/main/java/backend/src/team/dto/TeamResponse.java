package backend.src.team.dto;

import backend.src.player.dto.PlayerSummary;
import backend.src.team.Team;

import java.time.LocalDate;
import java.util.List;

public record TeamResponse(
        Integer id,
        String name,
        LocalDate foundationDate,
        String logoUrl,
        String ownerName,
        List<PlayerSummary> players,
        long matchesCount
) {
    public static TeamResponse from(Team team) {
        String ownerName = team.getOwner() != null
                ? team.getOwner().getName() + " " + team.getOwner().getSurname()
                : null;
        List<PlayerSummary> players = team.getPlayers() == null
                ? List.of()
                : team.getPlayers().stream()
                        .filter(p -> p.isActive())
                        .map(PlayerSummary::from)
                        .toList();
        long matchesCount = team.getMatches() == null ? 0 : team.getMatches().size();
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getFoundationDate(),
                team.getLogoUrl(),
                ownerName,
                players,
                matchesCount
        );
    }
}
