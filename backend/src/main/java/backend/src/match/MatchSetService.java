package backend.src.match;

import backend.src.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class MatchSetService {

    private final MatchSetRepository matchSetRepository;

    public MatchSetService(MatchSetRepository matchSetRepository) {
        this.matchSetRepository = matchSetRepository;
    }

    @Transactional(readOnly = true)
    public List<MatchSet> findAll() {
        return matchSetRepository.findAll();
    }

    @Transactional(readOnly = true)
    public MatchSet findById(Integer id) {
        return matchSetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MatchSet", "id", id));
    }

    @Transactional
    public MatchSet create(MatchSet matchSet) {
        return matchSetRepository.save(matchSet);
    }

    @Transactional
    public MatchSet update(@Valid MatchSet matchSet, Integer id) {
        // TODO Se tiene que comprobar si el que lo updatea es ??
        MatchSet existing = findById(id);
        BeanUtils.copyProperties(matchSet, existing, "id");
        return matchSetRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        // TODO Se tiene que comprobar si el que lo borra es ??
        MatchSet matchSet = findById(id);
        matchSetRepository.delete(matchSet);
    }
}
