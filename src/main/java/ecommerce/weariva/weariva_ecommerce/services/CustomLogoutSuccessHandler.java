package ecommerce.weariva.weariva_ecommerce.services;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import ecommerce.weariva.weariva_ecommerce.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final UserService userService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        if (!ObjectUtils.isEmpty(authentication)) {
            User user = this.userService.getUserByUsername(authentication.getName()).orElse(null);
            if (!ObjectUtils.isEmpty(user)) {
                user.setLoggedIn(false);
                this.userService.saveUser(user);
            }
        }
        response.sendRedirect("signin?logout");
    }

}
