package ecommerce.weariva.weariva_ecommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecommerce.weariva.weariva_ecommerce.models.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    public boolean existsByName(String productName);

    public List<Product> findByCategory(String category);

    public Optional<Product> findByName(String name);
}
