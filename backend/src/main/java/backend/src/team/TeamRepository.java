package backend.src.team;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {

    Optional<Team> findByName(String name);

    List<Team> findByOwnerIdOrderByIdAsc(Integer ownerId);

    Optional<Team> findFirstByPlayersId(Integer playerId);

    boolean existsByOwnerId(Integer ownerId);
}
