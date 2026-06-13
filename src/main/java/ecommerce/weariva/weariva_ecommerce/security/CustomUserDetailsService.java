package ecommerce.weariva.weariva_ecommerce.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ecommerce.weariva.weariva_ecommerce.user.models.User;
import ecommerce.weariva.weariva_ecommerce.user.services.UserServiceImplementation;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserServiceImplementation userServiceImpl;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Optional<User> userOptional = this.userServiceImpl.getUserByUsername(username);
            return userOptional.map(CustomUserDetails::new)
                    .orElseThrow(() -> new UsernameNotFoundException("Authentication Error"));
        } catch (Exception e) {
            return null;
        }
    }

}
