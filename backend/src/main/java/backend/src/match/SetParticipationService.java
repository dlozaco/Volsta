package backend.src.match;

import backend.src.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetParticipationService {

    private final SetParticipationRepository setParticipationRepository;

    public SetParticipationService(SetParticipationRepository setParticipationRepository) {
        this.setParticipationRepository = setParticipationRepository;
    }

    @Transactional(readOnly = true)
    public List<SetParticipation> findAll() {
        return setParticipationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public SetParticipation findById(Integer id) {
        return setParticipationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SetParticipation", "id", id));
    }

    @Transactional(readOnly = true)
    public List<SetParticipation> findByPlayerId(Integer playerId) {
        return setParticipationRepository.findByPlayerId(playerId);
    }

    @Transactional(readOnly = true)
    public List<SetParticipation> findByMatchSetId(Integer matchSetId) {
        return setParticipationRepository.findByMatchSetId(matchSetId);
    }

    @Transactional
    public SetParticipation create(SetParticipation setParticipation) {
        return setParticipationRepository.save(setParticipation);
    }

    @Transactional
    public SetParticipation update(@Valid SetParticipation setParticipation, Integer id) {
        SetParticipation existing = findById(id);
        BeanUtils.copyProperties(setParticipation, existing, "id");
        return setParticipationRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        SetParticipation setParticipation = findById(id);
        setParticipationRepository.delete(setParticipation);
    }
}
