package ecommerce.weariva.weariva_ecommerce.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecommerce.weariva.weariva_ecommerce.product.models.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    public boolean existsByName(String productName);

    public List<Product> findByCategory(String category);

    public Optional<Product> findByName(String name);
}
