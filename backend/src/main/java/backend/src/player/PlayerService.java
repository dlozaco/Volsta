package backend.src.player;

import backend.src.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional(readOnly = true)
    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Player findById(Integer id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player", "id", id));
    }

    @Transactional(readOnly = true)
    public Player findByEmail(String email) {
        return playerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Player", "email", email));
    }

    @Transactional(readOnly = true)
    public Player findByName(String name) {
        return playerRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Player", "name", name));
    }

    @Transactional(readOnly = true)
    public List<Player> findByCorePosition(PositionType corePosition) {
        return playerRepository.findByCorePosition(corePosition);
    }

    @Transactional
    public Player create(Player player) {
        return playerRepository.save(player);
    }

    @Transactional
    public Player update(@Valid Player player, Integer id) {
        Player existing = findById(id);
        BeanUtils.copyProperties(player, existing, "id");
        return playerRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Player player = findById(id);
        playerRepository.delete(player);
    }
}
