package ecommerce.weariva.weariva_ecommerce.controllers;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ecommerce.weariva.weariva_ecommerce.enums.MyOrdersStatus;
import ecommerce.weariva.weariva_ecommerce.models.Cart;
import ecommerce.weariva.weariva_ecommerce.models.Orders;
import ecommerce.weariva.weariva_ecommerce.models.Product;
import ecommerce.weariva.weariva_ecommerce.models.Rating;
import ecommerce.weariva.weariva_ecommerce.models.User;
import ecommerce.weariva.weariva_ecommerce.services.CartService;
import ecommerce.weariva.weariva_ecommerce.services.OrderService;
import ecommerce.weariva.weariva_ecommerce.services.ProductService;
import ecommerce.weariva.weariva_ecommerce.services.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {
    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;
    private final PasswordEncoder passwordEncoder;
    private final OrderService orderService;

    public User getAuthenticatedUser() {
        return this.userService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName())
                .orElse(null);
    }

    @ModelAttribute
    public void getCurrentUserInfo(Model model) {
        User user = this.getAuthenticatedUser();
        if (!ObjectUtils.isEmpty(user)) {
            model.addAttribute("authenticatedUser", user);
            model.addAttribute("cartCount", this.cartService.cartCount(user));

        }
    }

    private double getCartItemTotalAmount(List<Cart> cartItems) {
        return cartItems.stream()
                .mapToDouble(c -> c.getQuantity() * c.getProduct().getDiscountedPrice())
                .sum();
    }

    @GetMapping("/view-order-transactions")
    public String viewOrderTransactions(Model model){
        User user = this.getAuthenticatedUser();
        List<Orders> userOrders = this.orderService.getAllOrdersByUser(user);
        model.addAttribute("orderTransactions",userOrders);
        return "/ordertransactions";
    }

    @GetMapping("/view-order/{id}")
    public String orderView(@PathVariable Long id, Model model) {
        model.addAttribute("order", this.orderService.getMyOrderById(id).orElse(null));
        return "/viewinvoice";
    }

    @GetMapping("/cartdata")
    public String getCartData(Model model) {
        User user = this.getAuthenticatedUser();
        List<Cart> allCartItemsByUser = this.cartService.getAllCartItemsByUser(user);
        model.addAttribute("cartData", allCartItemsByUser);
        model.addAttribute("totalcartvalue", this.getCartItemTotalAmount(allCartItemsByUser));
        return "user/cartdisplay";
    }

    @GetMapping("/checkout")
    public String getOrder(Model model) {
        User user = this.getAuthenticatedUser();
        if (!ObjectUtils.isEmpty(user)) {
            List<Cart> cartItems = this.cartService.getAllCartItemsByUser(user);
            if (!(cartItems.size() > 0)) {
                return "redirect:/user/cartdata";
            }
            model.addAttribute("user", user);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalcartvalue", this.getCartItemTotalAmount(cartItems));
        }
        return "user/checkout";

    }

    @GetMapping("/myorders")
    public String getMyOrders(Model model) {
        User user = this.getAuthenticatedUser();
        if (!ObjectUtils.isEmpty(user)) {
            List<Orders> allOrdersByUser = this.orderService.getAllOrdersByUser(user);
            allOrdersByUser = allOrdersByUser.stream().sorted((a, b) -> b.getOrderId().compareTo(a.getOrderId()))
                    .toList();

            model.addAttribute("allOrdersSize", allOrdersByUser.size());
            model.addAttribute("allOrders", allOrdersByUser);
        } else {
            model.addAttribute("allOrdersSize", 0);
        }
        return "/user/myorders";
    }

    @GetMapping("/editprofile")
    public String editProfile() {
        return "user/editProfile";
    }

    @GetMapping("/updatstatus/{status}/{orderId}")
    public String getUpdateState(@PathVariable String status, @PathVariable Long orderId) {

        Orders myorder = this.orderService.getMyOrderById(orderId).orElse(null);
        System.out.println(status);
        if (!ObjectUtils.isEmpty(myorder)) {
            for (MyOrdersStatus changeStatus : MyOrdersStatus.values()) {
                if(status.equals(changeStatus.toString())){
                    myorder.setOrderStatus(changeStatus);
                    break;
                }
            }
            this.orderService.saveOrder(myorder);

        }
        return "redirect:/user/myorders";
    }


    @GetMapping("/updateCartItems/{update}/{productId}")
    public String updateCartItem(@PathVariable String update, @PathVariable Long productId) {
        User user = this.getAuthenticatedUser();
        Product product = this.productService.getProductById(productId);
        if (!ObjectUtils.isEmpty(user) && !ObjectUtils.isEmpty(product)) {
            Cart cartItem = this.cartService.getCartItemByUserAndProduct(user, product).orElse(null);
            if (!ObjectUtils.isEmpty(cartItem)) {
                if (update.equalsIgnoreCase("sub")) {
                    if (cartItem.getQuantity() > 1) {
                        cartItem.setQuantity(cartItem.getQuantity() - 1);
                        this.cartService.saveCart(cartItem);
                    } else {
                        this.cartService.deleteCartItem(cartItem);
                    }
                } else {
                    cartItem.setQuantity(cartItem.getQuantity() + 1);
                    this.cartService.saveCart(cartItem);
                }
            }
        }
        return "redirect:/user/cartdata";
    }

    @PostMapping("/rating/{id}")
    public String setRating(@ModelAttribute Rating rating, @PathVariable Long id, Model model) {

        User user = this.userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElse(null);
        if (user != null) {
            Product productById = this.productService.getProductById(id);
            if (!ObjectUtils.isEmpty(productById)) {
                productById.getRating().add(
                        new Rating(user.getName(),
                                rating.getRating(),
                                rating.getReview()));
                Product saveProduct = this.productService.saveProduct(productById);
                System.out.println(saveProduct.getRating());
            }
        }

        return "redirect:/productdetails/" + id;
    }

    @GetMapping("/profiledetails")
    public String place() {
        return "/user/userprofile";
    }

    @PostMapping("/resetPassword")
    public String changedPassword(@RequestParam String newpassword, Model model) {
        User user = this.getAuthenticatedUser();
        user.setPassword(passwordEncoder.encode(newpassword));
        userService.saveUser(user);
        return "redirect:/user/profiledetails";

    }

    @GetMapping("signout")
    public String signout() {
        return "logout";
    }

}
