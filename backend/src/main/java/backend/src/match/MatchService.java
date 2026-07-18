package backend.src.match;

import backend.src.exceptions.ResourceNotFoundException;
import backend.src.notes.Notes;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Transactional(readOnly = true)
    public List<Match> findAll() {
        return matchRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Match findById(Integer id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match", "id", id));
    }

    @Transactional(readOnly = true)
    public List<Match> findByMatchType(MatchType matchType) {
        return matchRepository.findByMatchType(matchType);
    }

    @Transactional(readOnly = true)
    public List<Match> findByTeamId(Integer teamId) {
        return matchRepository.findByLocalTeamIdOrVisitorTeamId(teamId, teamId);
    }

    @Transactional(readOnly = true)
    public List<MatchSet>findMatchSetsById(Integer matchId) {
        return findById(matchId).getSets();
    }

    @Transactional(readOnly = true)
    public List<Notes>findMatchNotesById(Integer matchId) {
        return findById(matchId).getNotes();
    }

    @Transactional
    public Match create(Match match) {
        return matchRepository.save(match);
    }

    @Transactional
    public Match update(@Valid Match match, Integer id) {
        Match existing = findById(id);
        BeanUtils.copyProperties(match, existing, "id");
        return matchRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Match match = findById(id);
        matchRepository.delete(match);
    }
}
