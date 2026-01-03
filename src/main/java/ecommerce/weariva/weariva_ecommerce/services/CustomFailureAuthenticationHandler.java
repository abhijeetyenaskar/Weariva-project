package ecommerce.weariva.weariva_ecommerce.services;

import java.io.IOException;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import ecommerce.weariva.weariva_ecommerce.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomFailureAuthenticationHandler extends SimpleUrlAuthenticationFailureHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        User user = this.userService.getUserByUsername(request.getParameter("username")).orElse(null);
        if (!ObjectUtils.isEmpty(user)) {
            if (user.isUserActive()) {
                if (user.isAccountNonLocked()) { 
                    userService.increaseFailures(exception, user); 
                } else {
                    exception = new LockedException("Wait, It'll be unlock after certain Timeout!!");
                }
            } else {
                exception = new LockedException(" Account is Deactivated!");
            }
        } else {
            exception = new LockedException("Account not found. Please Signup!");
        }

        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);
    }

}