package ecommerce.weariva.weariva_ecommerce.user.advices;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import ecommerce.weariva.weariva_ecommerce.cart.services.CartService;
import ecommerce.weariva.weariva_ecommerce.user.models.User;
import ecommerce.weariva.weariva_ecommerce.user.services.UserService;

@ControllerAdvice
public class AuthControllerAdvice {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @ModelAttribute
    public void getCurrentUserInfo(Model model, Principal principal) {

        User user = userService.getUserByUsername(principal.getName())
                .orElse(null);
        if (!ObjectUtils.isEmpty(user)) {
            model.addAttribute("authenticatedUser", user);
            if (!user.getRoles().contains("DELIVERY")) {
                model.addAttribute("cartCount", cartService.cartCount(user));
            }
        }
    }
}