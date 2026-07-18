package backend.src.player;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.validation.constraints.NotBlank;

public interface PlayerRepository extends JpaRepository<Player, Integer> {

    Optional<Player> findByEmail(String email);

    List<Player> findByCorePosition(PositionType corePosition);

    Optional<Player> findByName(@NotBlank String name);
}
