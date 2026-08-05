package backend.src.player;

import backend.src.exceptions.BusinessException;
import backend.src.exceptions.ResourceNotFoundException;
import backend.src.manager.Manager;
import backend.src.manager.ManagerRepository;
import backend.src.player.dto.PlayerRequest;
import backend.src.player.dto.PlayerSummary;
import backend.src.player.dto.PlayerUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlayerServiceTest {

    private final PlayerService playerService;
    private final ManagerRepository managerRepository;

    @Autowired
    public PlayerServiceTest(PlayerService playerService, ManagerRepository managerRepository) {
        this.playerService = playerService;
        this.managerRepository = managerRepository;
    }

    private Manager ownerOfTeam1() {
        return managerRepository.findById(1).orElseThrow();
    }

    private Manager ownerOfTeam2() {
        return managerRepository.findById(2).orElseThrow();
    }

    @Test
    @Transactional
    void shouldListActivePlayersByTeam() {
        assertEquals(6, playerService.listByTeam(1).size());
    }

    @Test
    @Transactional
    void shouldFindPlayerById_RightId() {
        PlayerSummary player = playerService.findById(1);
        assertEquals("David Lozano", player.name());
    }

    @Test
    @Transactional
    void shouldFindPlayerById_WrongId_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> playerService.findById(-1));
    }

    @Test
    @Transactional
    void shouldCreatePlayer_AsOwner_ReturnsOk() {
        PlayerRequest request = new PlayerRequest("Test", "User", "test.user@test.com", 99, PositionType.LIBERO);

        PlayerSummary created = playerService.create(1, request, ownerOfTeam1());

        assertNotNull(created.id());
        assertEquals(7, playerService.listByTeam(1).size());
    }

    @Test
    @Transactional
    void shouldRejectCreatePlayer_RepeatedDorsal() {
        PlayerRequest request = new PlayerRequest("Test", "User", "test.user@test.com", 10, PositionType.LIBERO);

        assertThrows(BusinessException.class, () -> playerService.create(1, request, ownerOfTeam1()));
    }

    @Test
    @Transactional
    void shouldRejectCreatePlayer_NonOwner() {
        PlayerRequest request = new PlayerRequest("Test", "User", "test.user@test.com", 99, PositionType.LIBERO);

        assertThrows(AccessDeniedException.class, () -> playerService.create(1, request, ownerOfTeam2()));
    }

    @Test
    @Transactional
    void shouldUpdatePlayer_AsOwner_ReturnsOk() {
        PlayerUpdateRequest request = new PlayerUpdateRequest("Updated", "Name", "david.lozano@quokka.es", 10, PositionType.SETTER);

        PlayerSummary updated = playerService.update(1, request, ownerOfTeam1());

        assertEquals("Updated", updated.name());
        assertEquals("Updated", playerService.findById(1).name());
    }

    @Test
    @Transactional
    void shouldRejectUpdatePlayer_NonOwner() {
        PlayerUpdateRequest request = new PlayerUpdateRequest("Hacked", "Name", "david.lozano@quokka.es", 10, PositionType.SETTER);

        assertThrows(AccessDeniedException.class, () -> playerService.update(1, request, ownerOfTeam2()));
    }

    @Test
    @Transactional
    void shouldSoftDeletePlayer_RemovesFromActiveRoster() {
        assertEquals(6, playerService.listByTeam(1).size());

        playerService.softDelete(1, ownerOfTeam1());

        assertEquals(5, playerService.listByTeam(1).size());
        assertFalse(playerService.findById(1).active());
    }
}
