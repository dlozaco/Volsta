package backend.src.notes;

import backend.src.exceptions.ResourceNotFoundException;
import backend.src.player.Player;
import backend.src.player.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NotesServiceTest {

    private final NotesService notesService;
    private final PlayerRepository playerRepository;

    @Autowired
    public NotesServiceTest(NotesService notesService, PlayerRepository playerRepository) {
        this.notesService = notesService;
        this.playerRepository = playerRepository;
    }

    @Test
    void shouldFindAllNotes() {
        assertEquals(12, notesService.findAll().size());
    }

    @Test
    void shouldFindNotesById_RightId() {
        Notes notes = notesService.findById(1);
        assertEquals("Mejora en defensa", notes.getSubject());
    }

    @Test
    void shouldFindNotesById_WrongId_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> notesService.findById(-1));
    }

    @Test
    void shouldFindNotesByPlayerId() {
        assertEquals(2, notesService.findByPlayerId(1).size());
    }

    @Test
    @Transactional
    void shouldCreateNotes_ReturnsOk() {
        int count = notesService.findAll().size();
        Player player = playerRepository.findById(1).orElseThrow();

        Notes notes = new Notes();
        notes.setSubject("Test subject");
        notes.setDescription("Test description");
        notes.setPlayer(player);
        notesService.create(notes);

        assertNotNull(notes.getId());
        assertEquals(count + 1, notesService.findAll().size());
    }

    @Test
    @Transactional
    void shouldUpdateNotes_RightId_ReturnsOk() {
        Notes notes = notesService.findById(1);
        notes.setSubject("Updated subject");
        notesService.update(notes, 1);

        Notes updated = notesService.findById(1);
        assertEquals("Updated subject", updated.getSubject());
    }

    @Test
    @Transactional
    void shouldDeleteNotes_ReturnsVoid() {
        int count = notesService.findAll().size();
        Player player = playerRepository.findById(1).orElseThrow();

        Notes notes = new Notes();
        notes.setSubject("Temp");
        notes.setDescription("Temp desc");
        notes.setPlayer(player);
        notesService.create(notes);

        assertEquals(count + 1, notesService.findAll().size());
        notesService.delete(notes.getId());
        assertEquals(count, notesService.findAll().size());
    }
}
