package backend.src.match;

import backend.src.exceptions.ResourceNotFoundException;
import backend.src.player.Player;
import backend.src.player.PlayerRepository;
import backend.src.player.PositionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SetParticipationServiceTest {

    private final SetParticipationService setParticipationService;
    private final PlayerRepository playerRepository;
    private final MatchSetService matchSetService;

    @Autowired
    public SetParticipationServiceTest(SetParticipationService setParticipationService, PlayerRepository playerRepository, MatchSetService matchSetService) {
        this.setParticipationService = setParticipationService;
        this.playerRepository = playerRepository;
        this.matchSetService = matchSetService;
    }

    @Test
    void shouldFindAllSetParticipations() {
        assertEquals(144, setParticipationService.findAll().size());
    }

    @Test
    void shouldFindSetParticipationById_RightId() {
        SetParticipation sp = setParticipationService.findById(1);
        assertEquals(8, sp.getPoints());
        assertEquals(2, sp.getFaults());
    }

    @Test
    void shouldFindSetParticipationById_WrongId_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> setParticipationService.findById(-1));
    }

    @Test
    void shouldFindByPlayerId() {
        assertEquals(12, setParticipationService.findByPlayerId(1).size());
    }

    @Test
    void shouldFindByMatchSetId() {
        assertEquals(12, setParticipationService.findByMatchSetId(1).size());
    }

    @Test
    @Transactional
    void shouldCreateSetParticipation_ReturnsOk() {
        int count = setParticipationService.findAll().size();
        Player player = playerRepository.findById(1).orElseThrow();
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
        Player player = playerRepository.findById(2).orElseThrow();
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
