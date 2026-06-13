package ecommerce.weariva.weariva_ecommerce.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecommerce.weariva.weariva_ecommerce.cart.models.Cart;
import ecommerce.weariva.weariva_ecommerce.product.models.Product;
import ecommerce.weariva.weariva_ecommerce.user.models.User;

import java.util.Optional;
import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserAndProduct(User user, Product product);

    public Integer countByUser(User user);

    List<Cart> findByUser(User user);

    void deleteByUser(User user);
}
