package backend.src.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetParticipationRepository extends JpaRepository<SetParticipation, Integer> {

    List<SetParticipation> findByPlayerId(Integer playerId);

    List<SetParticipation> findByMatchSetId(Integer matchSetId);
}
