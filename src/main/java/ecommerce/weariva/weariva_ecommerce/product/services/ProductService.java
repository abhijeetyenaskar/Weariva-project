package ecommerce.weariva.weariva_ecommerce.product.services;

import java.util.List;

import org.springframework.stereotype.Service;

import ecommerce.weariva.weariva_ecommerce.product.models.Product;

@Service
public interface ProductService {

    public boolean existsByProductName(String productName);

    public Product saveProduct(Product product);

    public List<Product> getAllProducts();

    public Product getProductById(Long id);

    public boolean deleteProductById(Long id);

    public Product findProductByName(String productName);

    public List<Product> getAllProductsByCategory(String category);
}
