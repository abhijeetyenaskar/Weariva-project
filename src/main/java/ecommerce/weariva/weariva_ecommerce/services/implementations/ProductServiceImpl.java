package ecommerce.weariva.weariva_ecommerce.services.implementations;

import java.util.List;

import org.springframework.stereotype.Service;

import ecommerce.weariva.weariva_ecommerce.models.Product;
import ecommerce.weariva.weariva_ecommerce.repositories.ProductRepository;
import ecommerce.weariva.weariva_ecommerce.services.ProductService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public boolean existsByProductName(String productName) {
        return this.productRepository.existsByName(productName);

    }

    @Override
    public Product saveProduct(Product product) {
        return this.productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return this.productRepository.findById(id).orElse(null);
    }

    @Override
    public boolean deleteProductById(Long id) {
        if (this.productRepository.existsById(id)) {
            this.productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Product> getAllProductsByCategory(String category) {
        return this.productRepository.findByCategory(category);
    }

    @Override
    public Product findProductByName(String productName) {
        return this.productRepository.findByName(productName).orElse(null);
    }
}
