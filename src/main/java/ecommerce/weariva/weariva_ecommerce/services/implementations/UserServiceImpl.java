package ecommerce.weariva.weariva_ecommerce.services.implementations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import ecommerce.weariva.weariva_ecommerce.models.User;
import ecommerce.weariva.weariva_ecommerce.repositories.UserRepository;
import ecommerce.weariva.weariva_ecommerce.services.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> getUserByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public boolean existByCurrentUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }

    @Override
    public List<User> getAllUserByRoles(String role) {
        return this.userRepository.findByRoles(role);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return this.userRepository.findById(id);
    }

    @Override
    public void increaseFailures(AuthenticationException exception, User user) {
        int failureCount = user.getFailedCount() + 1;
        if (failureCount < 3) {
            user.setFailedCount(failureCount);
            this.userRepository.save(user);
        } else {
            user.setAccountNonLocked(false);
            user.setUserLockedTime(LocalDateTime.now());
            user.setFailedCount(0);
            this.userRepository.save(user);
            exception = new LockedException("Account has been Locked after 3 Attempts!<br>Unlock after 1 min");
        }
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public void deleteUser(Long userId) {
        this.userRepository.deleteById(userId);
    }

    @Override
    @Scheduled(fixedRate = 40000)
    public void unlockedTimeExpired() {
        List<User> lockedUser = this.userRepository.findByIsAccountNonLockedFalseAndUserLockedTimeIsNotNull();
        if (lockedUser.size() > 0) {
            lockedUser.forEach(user -> {
                if (user.getUserLockedTime().plusSeconds(30).isBefore(LocalDateTime.now())) {
                    user.setAccountNonLocked(true);
                    user.setFailedCount(0);
                    user.setUserLockedTime(null);
                    this.saveUser(user);
                }
            });
        }
    }

}
