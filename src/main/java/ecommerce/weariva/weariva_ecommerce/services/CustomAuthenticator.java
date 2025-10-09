package ecommerce.weariva.weariva_ecommerce.services;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import ecommerce.weariva.weariva_ecommerce.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomAuthenticator implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
                
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<String> authorityListToSet = AuthorityUtils.authorityListToSet(authorities);

        User user = this.userService.getUserByUsername(authentication.getName()).orElse(null);
        if (!ObjectUtils.isEmpty(user)) {
            user.setLoggedIn(true);
            user.setFailedCount(0);
            this.userService.saveUser(user);
            /* After Login Succcessfully, these step decides where to redirect. */
            if (authorityListToSet.contains("ADMIN")) {
                response.sendRedirect("/admin");
            } else {
                response.sendRedirect("/");
            }
        }

    }

}
