package ecommerce.weariva.weariva_ecommerce.services.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ecommerce.weariva.weariva_ecommerce.models.Cart;
import ecommerce.weariva.weariva_ecommerce.models.Product;
import ecommerce.weariva.weariva_ecommerce.models.User;
import ecommerce.weariva.weariva_ecommerce.repositories.CartRepository;
import ecommerce.weariva.weariva_ecommerce.services.CartService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImple implements CartService {

    private final CartRepository cartRepository;

    @Override
    public Optional<Cart> getCartItemByUserAndProduct(User user, Product product) {
        return this.cartRepository.findByUserAndProduct(user, product);
    }

    @Override
    public Cart saveCart(Cart cart) {
        return this.cartRepository.save(cart);
    }

    @Override
    public void deleteCartItem(Cart cartItem) {
        this.cartRepository.delete(cartItem);
    }

    @Override
    public Integer cartCount(User user) {
        return this.cartRepository.countByUser(user);
    }

    @Override
    public List<Cart> getAllCartItemsByUser(User user) {
        return this.cartRepository.findByUser(user);
    }

    @Override
    public boolean deleteCartItems(List<Cart> cartItems) {
        if (cartItems.size() > 0) {
            cartItems.stream().forEach(this::deleteCartItem);
            return true;
        } else {
            return false;
        }

    }

}
