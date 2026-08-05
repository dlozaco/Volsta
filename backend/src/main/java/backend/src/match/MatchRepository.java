package backend.src.match;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Integer> {

    List<Match> findByMatchType(MatchType matchType);

    List<Match> findByLocalTeamIdOrVisitorTeamId(Integer localTeamId, Integer visitorTeamId);

    boolean existsByLocalTeamIdOrVisitorTeamId(Integer localTeamId, Integer visitorTeamId);

    long countByLocalTeamIdOrVisitorTeamId(Integer localTeamId, Integer visitorTeamId);
}
