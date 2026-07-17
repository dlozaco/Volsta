package backend.src.manager;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Integer> {

    Optional<Manager> findByEmail(String email);

    Optional<Manager> findByPhoneNumber(String phoneNumber);

    Optional<Manager> findByUserId(Integer userId);
}
