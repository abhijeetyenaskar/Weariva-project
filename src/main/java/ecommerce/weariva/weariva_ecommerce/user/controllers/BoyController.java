package ecommerce.weariva.weariva_ecommerce.user.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/boy")
@RequiredArgsConstructor
public class BoyController {

    
    @GetMapping("profiledetails")
    public String boyProfileDetails() {
        return "/boys/boyprofile";
    }

    @GetMapping("editprofile")
    public String boyEditProfile() {
        return "/boys/editprofile";
    }

    
}
