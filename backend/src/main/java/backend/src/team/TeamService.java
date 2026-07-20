package backend.src.team;

import backend.src.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Transactional(readOnly = true)
    public Page<Team> findAll(Pageable pageable) {
        return teamRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Team findById(Integer id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));
    }

    @Transactional(readOnly = true)
    public Team findByName(String name) {
        return teamRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "name", name));
    }

    @Transactional
    public Team create(Team team) {
        return teamRepository.save(team);
    }

    @Transactional
    public Team update(@Valid Team team, Integer id) {
        // TODO Se tiene que comprobar si el que lo actualiza es propietario
        Team existing = findById(id);
        BeanUtils.copyProperties(team, existing, "id");
        return teamRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        // TODO Se tiene que comprobar si el que lo borra es propietario
        Team team = findById(id);
        teamRepository.delete(team);
    }
}
