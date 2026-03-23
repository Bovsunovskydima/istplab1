package stp.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stp.lab1.model.entity.User;
import stp.lab1.model.enums.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
}
