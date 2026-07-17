package backend.src.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthoritiesRepository extends JpaRepository<Authorities, Integer> {


    Optional<Authorities> findByAuthority(String authority);
}
