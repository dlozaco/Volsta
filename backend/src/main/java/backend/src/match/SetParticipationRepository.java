package backend.src.match;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SetParticipationRepository extends JpaRepository<SetParticipation, Integer> {

    List<SetParticipation> findByPlayerId(Integer playerId);

    List<SetParticipation> findByMatchSetId(Integer matchSetId);
}
