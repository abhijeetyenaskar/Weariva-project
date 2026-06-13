package ecommerce.weariva.weariva_ecommerce.cart.restcontrollers;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ecommerce.weariva.weariva_ecommerce.cart.models.Cart;
import ecommerce.weariva.weariva_ecommerce.cart.services.CartService;
import ecommerce.weariva.weariva_ecommerce.common.records.RestApiResponse;
import ecommerce.weariva.weariva_ecommerce.product.models.Product;
import ecommerce.weariva.weariva_ecommerce.product.services.ProductService;
import ecommerce.weariva.weariva_ecommerce.user.models.User;
import ecommerce.weariva.weariva_ecommerce.user.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class CartRestController {

    private final UserService userService;
    private final CartService cartService;
    private final ProductService productService;

    @PostMapping("/user/addtocart")
    public ResponseEntity<?> cartAdd(@RequestParam Long productId, Principal principal) {
        try {
            User user = this.userService.getUserByUsername(principal.getName()).orElse(null);
            Product product = this.productService.getProductById(productId);
            if (!ObjectUtils.isEmpty(user) && !ObjectUtils.isEmpty(product)) {
                Cart cartItem = this.cartService.getCartItemByUserAndProduct(user, product).orElse(null);
                if (!ObjectUtils.isEmpty(cartItem)) {
                    cartItem.setQuantity(cartItem.getQuantity() + 1);
                    this.cartService.saveCart(cartItem);
                } else {
                    this.cartService.saveCart(Cart.builder().user(user).product(product).quantity(1).build());
                }
            }
            return ResponseEntity.ok().body(new RestApiResponse(true, "Incremented"));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new RestApiResponse(false, "Failed Incremented"));
        }
    }

    @PostMapping("/user/deletecartitem")
    public ResponseEntity<?> cartDelete(@RequestParam Long productId, Principal principal) {
        try {
            User user = this.userService.getUserByUsername(principal.getName()).orElse(null);
            Product product = this.productService.getProductById(productId);
            if (!ObjectUtils.isEmpty(user) && !ObjectUtils.isEmpty(product)) {
                Cart cartItem = this.cartService.getCartItemByUserAndProduct(user, product).orElse(null);
                if (!ObjectUtils.isEmpty(cartItem) && cartItem.getQuantity() > 1) {
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                    this.cartService.saveCart(cartItem);
                } else {
                    this.cartService.deleteCartItem(cartItem);
                }
            }
            return ResponseEntity.ok().body(new RestApiResponse(true, "Decremented"));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new RestApiResponse(false, "Failed Decremented"));
        }
    }

}
