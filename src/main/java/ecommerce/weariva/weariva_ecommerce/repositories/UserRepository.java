package ecommerce.weariva.weariva_ecommerce.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecommerce.weariva.weariva_ecommerce.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findByUsername(String username);
    public Optional<User> findByEmail(String email);

    public boolean existsByUsername(String username);

    public List<User> findByIsAccountNonLockedFalse();
    public List<User> findByIsAccountNonLockedFalseAndUserLockedTimeIsNotNull();

    public List<User> findByRoles(String roles);

}
