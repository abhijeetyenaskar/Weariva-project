package ecommerce.weariva.weariva_ecommerce.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.AuthenticationException;

import ecommerce.weariva.weariva_ecommerce.models.User;

public interface UserService {

    public Optional<User> getUserByUsername(String username);

    public Optional<User> getUserByEmail(String email);

    public Optional<User> getUserById(Long id);

    public User saveUser(User user);

    public boolean existByCurrentUsername(String username);

    public List<User> getAllUserByRoles(String role);

    public void increaseFailures(AuthenticationException exception, User user);

    public void deleteUser(Long userId);

    public void unlockedTimeExpired();

}
