package ecommerce.weariva.weariva_ecommerce.services;

import org.springframework.stereotype.Service;

import ecommerce.weariva.weariva_ecommerce.models.Cart;
import ecommerce.weariva.weariva_ecommerce.models.Product;
import ecommerce.weariva.weariva_ecommerce.models.User;

import java.util.Optional;
import java.util.List;

@Service
public interface CartService {
    public Optional<Cart> getCartItemByUserAndProduct(User user, Product product);

    public Cart saveCart(Cart cart);

    public void deleteCartItem(Cart cartItem);

    public Integer cartCount(User user);

    public List<Cart> getAllCartItemsByUser(User user);

    public boolean deleteCartItems(List<Cart> cartItems);
}
