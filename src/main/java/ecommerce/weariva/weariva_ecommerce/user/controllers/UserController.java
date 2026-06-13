package ecommerce.weariva.weariva_ecommerce.user.controllers;

import java.security.Principal;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ecommerce.weariva.weariva_ecommerce.user.models.User;
import ecommerce.weariva.weariva_ecommerce.user.services.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/editprofile")
    public String editProfile() {
        return "user/editProfile";
    }

    @GetMapping("/profiledetails")
    public String profiledetails() {
        return "/user/userprofile";
    }

    @PostMapping("/resetPassword")
    public String changedPassword(@RequestParam String newpassword, Model model, Principal principal) {
        User user = this.userService.getUserByUsername(principal.getName()).orElse(null);
        user.setPassword(passwordEncoder.encode(newpassword));
        userService.saveUser(user);
        return "redirect:/user/profiledetails";

    }

    @GetMapping("signout")
    public String signout() {
        return "logout";
    }

}
