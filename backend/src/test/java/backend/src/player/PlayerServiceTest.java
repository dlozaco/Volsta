package backend.src.player;

import backend.src.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlayerServiceTest {

    private final PlayerService playerService;

    @Autowired
    public PlayerServiceTest(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Test
    void shouldFindAllPlayers() {
        assertEquals(12, playerService.findAll().size());
    }

    @Test
    void shouldFindPlayerById_RightId() {
        Player player = playerService.findById(1);
        assertEquals("David Lozano", player.getName());
    }

    @Test
    void shouldFindPlayerById_WrongId_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> playerService.findById(-1));
    }

    @Test
    void shouldFindPlayerByEmail_RightEmail() {
        Player player = playerService.findByEmail("david.lozano@quokka.es");
        assertEquals(1, player.getId());
    }

    @Test
    void shouldFindPlayerByEmail_WrongEmail_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> playerService.findByEmail("nobody@test.com"));
    }

    @Test
    void shouldFindByCorePosition() {
        assertEquals(3, playerService.findByCorePosition(PositionType.MIDDLE_BLOCKER).size());
    }

    @Test
    @Transactional
    void shouldCreatePlayer_ReturnsOk() {
        int count = playerService.findAll().size();

        Player player = new Player();
        player.setName("Test");
        player.setSurname("User");
        player.setEmail("test.user@test.com");
        player.setCorePosition(PositionType.LIBERO);
        playerService.create(player);

        assertNotNull(player.getId());
        assertEquals(count + 1, playerService.findAll().size());
    }

    @Test
    @Transactional
    void shouldUpdatePlayer_RightId_ReturnsOk() {
        Player player = playerService.findById(1);
        player.setName("UpdatedName");
        playerService.update(player, 1);

        Player updated = playerService.findById(1);
        assertEquals("UpdatedName", updated.getName());
    }

    @Test
    @Transactional
    void shouldDeletePlayer_ReturnsVoid() {
        int count = playerService.findAll().size();

        Player player = new Player();
        player.setName("Temp");
        player.setSurname("Player");
        player.setEmail("temp.player@test.com");
        player.setCorePosition(PositionType.SETTER);
        playerService.create(player);

        assertEquals(count + 1, playerService.findAll().size());
        playerService.delete(player.getId());
        assertEquals(count, playerService.findAll().size());
    }
}
