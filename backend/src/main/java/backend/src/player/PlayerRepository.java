package backend.src.player;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

    Optional<Player> findByEmail(String email);

    List<Player> findByCorePosition(PositionType corePosition);

    Optional<Player> findByName(@NotBlank String name);
}
