package ecommerce.weariva.weariva_ecommerce.cart.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ecommerce.weariva.weariva_ecommerce.cart.models.Cart;
import ecommerce.weariva.weariva_ecommerce.cart.services.CartService;
import ecommerce.weariva.weariva_ecommerce.product.models.Product;
import ecommerce.weariva.weariva_ecommerce.product.services.ProductService;
import ecommerce.weariva.weariva_ecommerce.user.models.User;
import ecommerce.weariva.weariva_ecommerce.user.services.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;

    private double getCartItemTotalAmount(List<Cart> cartItems) {
        return cartItems.stream()
                .mapToDouble(c -> c.getQuantity() * c.getProduct().getDiscountedPrice())
                .sum();
    }

    @GetMapping("/checkout")
    public String getOrder(Model model, Principal principal) {
        User user = this.userService.getUserByUsername(principal.getName()).orElse(null);
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

    @GetMapping("/user/cartdata")
    public String getCartData(Model model, Principal principal) {

        List<Cart> allCartItemsByUser = this.cartService
                .getAllCartItemsByUser(this.userService.getUserByUsername(principal.getName()).orElse(null));
        model.addAttribute("cartData", allCartItemsByUser);
        model.addAttribute("totalcartvalue", this.getCartItemTotalAmount(allCartItemsByUser));
        return "user/cartdisplay";
    }

    @GetMapping("/user/updateCartItems/{update}/{productId}")
    public String updateCartItem(@PathVariable String update, @PathVariable Long productId, Principal principal) {
        User user = this.userService.getUserByUsername(principal.getName()).orElse(null);
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

}
