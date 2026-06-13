package ecommerce.weariva.weariva_ecommerce.cart.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ecommerce.weariva.weariva_ecommerce.cart.models.Cart;
import ecommerce.weariva.weariva_ecommerce.product.models.Product;
import ecommerce.weariva.weariva_ecommerce.user.models.User;

@Service
public interface CartService {
    public Optional<Cart> getCartItemByUserAndProduct(User user, Product product);

    public Cart saveCart(Cart cart);

    public void deleteCartItem(Cart cartItem);

    public Integer cartCount(User user);

    public List<Cart> getAllCartItemsByUser(User user);

    public boolean deleteCartItems(List<Cart> cartItems);
}
