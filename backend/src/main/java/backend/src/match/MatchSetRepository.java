package backend.src.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchSetRepository extends JpaRepository<MatchSet, Integer> {
}
