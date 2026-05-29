package backend.src.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {

    List<Match> findByMatchType(MatchType matchType);

    List<Match> findByLocalTeamIdOrVisitorTeamId(Integer localTeamId, Integer visitorTeamId);
}
