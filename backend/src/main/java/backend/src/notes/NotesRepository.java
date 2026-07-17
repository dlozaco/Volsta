package backend.src.notes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotesRepository extends JpaRepository<Notes, Integer> {

    List<Notes> findByPlayerId(Integer playerId);
}
