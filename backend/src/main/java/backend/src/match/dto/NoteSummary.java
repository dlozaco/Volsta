package backend.src.match.dto;

import backend.src.notes.Notes;

public record NoteSummary(
        Integer id,
        String subject,
        String description,
        Integer playerId,
        String playerName
) {
    public static NoteSummary from(Notes note) {
        String playerName = note.getPlayer() != null
                ? note.getPlayer().getName() + " " + note.getPlayer().getSurname()
                : null;
        return new NoteSummary(
                note.getId(),
                note.getSubject(),
                note.getDescription(),
                note.getPlayer() != null ? note.getPlayer().getId() : null,
                playerName
        );
    }
}
