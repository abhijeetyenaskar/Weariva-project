package ecommerce.weariva.weariva_ecommerce.controllers;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import ecommerce.weariva.weariva_ecommerce.models.Orders;
import ecommerce.weariva.weariva_ecommerce.models.User;
import ecommerce.weariva.weariva_ecommerce.services.OrderService;
import ecommerce.weariva.weariva_ecommerce.services.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/boy")
@RequiredArgsConstructor
public class BoyController {

    private final UserService userService;
    private final OrderService orderService;

    @ModelAttribute
    public void getCurrentUserInfo(Model model) {
        User user = this.userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElse(null);
        if (!ObjectUtils.isEmpty(user)) {
            model.addAttribute("authenticatedUser", user);
        }
    }

    @GetMapping("profiledetails")
    public String boyProfileDetails() {
        return "/boys/boyprofile";
    }

    @GetMapping("editprofile")
    public String boyEditProfile() {
        return "/boys/editprofile";
    }

    @GetMapping("myorders")
    public String boySpecifiedOrders(Model model) {

        List<Orders> deliveryBoyOrderList = this.orderService.getAllOrders().stream().filter(item -> item.getDeliveryAgent() == null || item
                .getDeliveryAgent().equals(SecurityContextHolder.getContext().getAuthentication().getName())).toList();
                model.addAttribute("allDeliveryOrders", deliveryBoyOrderList.size());

                List<Orders> list = deliveryBoyOrderList.stream()
                .filter(item -> item.getOrderStatus().toString().equals("OUT_FOR_DELIVERY")).toList();
        model.addAttribute("deliveryOrders",list );
        return "/boys/myorders";
    }
}
