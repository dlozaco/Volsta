package backend.src.match;

import backend.src.exceptions.ResourceNotFoundException;
import backend.src.player.Player;
import backend.src.player.PlayerService;
import backend.src.player.PositionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SetParticipationServiceTest {

    private final SetParticipationService setParticipationService;
    private final PlayerService playerService;
    private final MatchSetService matchSetService;

    @Autowired
    public SetParticipationServiceTest(SetParticipationService setParticipationService, PlayerService playerService, MatchSetService matchSetService) {
        this.setParticipationService = setParticipationService;
        this.playerService = playerService;
        this.matchSetService = matchSetService;
    }

    @Test
    void shouldFindAllSetParticipations() {
        assertEquals(20, setParticipationService.findAll().size());
    }

    @Test
    void shouldFindSetParticipationById_RightId() {
        SetParticipation sp = setParticipationService.findById(1);
        assertEquals(8, sp.getPoints());
    }

    @Test
    void shouldFindSetParticipationById_WrongId_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> setParticipationService.findById(-1));
    }

    @Test
    void shouldFindByPlayerId() {
        assertEquals(4, setParticipationService.findByPlayerId(1).size());
    }

    @Test
    void shouldFindByMatchSetId() {
        assertEquals(4, setParticipationService.findByMatchSetId(1).size());
    }

    @Test
    @Transactional
    void shouldCreateSetParticipation_ReturnsOk() {
        int count = setParticipationService.findAll().size();
        Player player = playerService.findById(1);
        MatchSet matchSet = matchSetService.findById(1);

        SetParticipation sp = new SetParticipation();
        sp.setPlayer(player);
        sp.setMatchSet(matchSet);
        sp.setPositionType(PositionType.MIDDLE_BLOCKER);
        sp.setPoints(5);
        setParticipationService.create(sp);

        assertNotNull(sp.getId());
        assertEquals(count + 1, setParticipationService.findAll().size());
    }

    @Test
    @Transactional
    void shouldUpdateSetParticipation_RightId_ReturnsOk() {
        SetParticipation sp = setParticipationService.findById(1);
        sp.setPoints(99);
        setParticipationService.update(sp, 1);

        SetParticipation updated = setParticipationService.findById(1);
        assertEquals(99, updated.getPoints());
    }

    @Test
    @Transactional
    void shouldDeleteSetParticipation_ReturnsVoid() {
        int count = setParticipationService.findAll().size();
        Player player = playerService.findById(2);
        MatchSet matchSet = matchSetService.findById(1);

        SetParticipation sp = new SetParticipation();
        sp.setPlayer(player);
        sp.setMatchSet(matchSet);
        sp.setPositionType(PositionType.SETTER);
        sp.setPoints(3);
        setParticipationService.create(sp);

        assertEquals(count + 1, setParticipationService.findAll().size());
        setParticipationService.delete(sp.getId());
        assertEquals(count, setParticipationService.findAll().size());
    }
}
