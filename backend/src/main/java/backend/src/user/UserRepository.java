package backend.src.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.authority.authority = :authority")
    List<User> findUsersByAuthority(String authority);

    Optional<User> findUserById(Integer id);

    Boolean existsByUsername(String username);
}
