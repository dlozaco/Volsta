package backend.src.player.dto;

import backend.src.player.Player;
import backend.src.player.PositionType;

public record PlayerSummary(
        Integer id,
        String name,
        String surname,
        String email,
        int dorsal,
        PositionType corePosition,
        boolean active
) {
    public static PlayerSummary from(Player player) {
        return new PlayerSummary(
                player.getId(),
                player.getName(),
                player.getSurname(),
                player.getEmail(),
                player.getDorsal(),
                player.getCorePosition(),
                player.isActive()
        );
    }
}
