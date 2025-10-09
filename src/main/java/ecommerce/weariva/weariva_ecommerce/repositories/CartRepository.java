package ecommerce.weariva.weariva_ecommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ecommerce.weariva.weariva_ecommerce.models.Cart;
import ecommerce.weariva.weariva_ecommerce.models.Product;
import ecommerce.weariva.weariva_ecommerce.models.User;

import java.util.Optional;
import java.util.List;


public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserAndProduct(User user, Product product);

    public Integer countByUser(User user);

    List<Cart> findByUser(User user);

    void deleteByUser(User user);
}
