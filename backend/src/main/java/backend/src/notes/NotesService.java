package backend.src.notes;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import backend.src.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;

@Service
@Validated
public class NotesService {

    private final NotesRepository notesRepository;

    public NotesService(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    @Transactional(readOnly = true)
    public List<Notes> findAll() {
        return notesRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Notes findById(Integer id) {
        return notesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notes", "id", id));
    }

    @Transactional(readOnly = true)
    public List<Notes> findByPlayerId(Integer playerId) {
        return notesRepository.findByPlayerId(playerId);
    }

    @Transactional
    public Notes create(Notes notes) {
        return notesRepository.save(notes);
    }

    @Transactional
    public Notes update(@Valid Notes notes, Integer id) {
        // TODO Se tiene que comprobar si el que lo updatea es él propietario del equipo
        Notes existing = findById(id);
        BeanUtils.copyProperties(notes, existing, "id");
        return notesRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        // TODO Se tiene que comprobar si el que lo updatea es él propietario del equipo
        Notes notes = findById(id);
        notesRepository.delete(notes);
    }
}
