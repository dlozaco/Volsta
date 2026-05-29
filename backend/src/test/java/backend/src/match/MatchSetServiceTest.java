package backend.src.match;

import backend.src.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MatchSetServiceTest {

    private final MatchSetService matchSetService;

    @Autowired
    public MatchSetServiceTest(MatchSetService matchSetService) {
        this.matchSetService = matchSetService;
    }

    @Test
    void shouldFindAllMatchSets() {
        assertEquals(12, matchSetService.findAll().size());
    }

    @Test
    void shouldFindMatchSetById_RightId() {
        MatchSet matchSet = matchSetService.findById(1);
        assertEquals(1, matchSet.getSetNumber());
        assertEquals(25, matchSet.getLocalTeamScore());
    }

    @Test
    void shouldFindMatchSetById_WrongId_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> matchSetService.findById(-1));
    }

    @Test
    @Transactional
    void shouldCreateMatchSet_ReturnsOk() {
        int count = matchSetService.findAll().size();

        MatchSet matchSet = new MatchSet();
        matchSet.setSetNumber(1);
        matchSet.setLocalTeamScore(25);
        matchSet.setVisitorTeamScore(20);
        matchSetService.create(matchSet);

        assertNotNull(matchSet.getId());
        assertEquals(count + 1, matchSetService.findAll().size());
    }

    @Test
    @Transactional
    void shouldUpdateMatchSet_RightId_ReturnsOk() {
        MatchSet matchSet = matchSetService.findById(1);
        matchSet.setLocalTeamScore(30);
        matchSetService.update(matchSet, 1);

        MatchSet updated = matchSetService.findById(1);
        assertEquals(30, updated.getLocalTeamScore());
    }

    @Test
    @Transactional
    void shouldDeleteMatchSet_ReturnsVoid() {
        int count = matchSetService.findAll().size();

        MatchSet matchSet = new MatchSet();
        matchSet.setSetNumber(5);
        matchSet.setLocalTeamScore(25);
        matchSet.setVisitorTeamScore(20);
        matchSetService.create(matchSet);

        assertEquals(count + 1, matchSetService.findAll().size());
        matchSetService.delete(matchSet.getId());
        assertEquals(count, matchSetService.findAll().size());
    }
}
