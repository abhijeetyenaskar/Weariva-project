package ecommerce.weariva.weariva_ecommerce.configuration;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import ecommerce.weariva.weariva_ecommerce.models.Product;
import ecommerce.weariva.weariva_ecommerce.models.Rating;
import ecommerce.weariva.weariva_ecommerce.models.User;
import ecommerce.weariva.weariva_ecommerce.repositories.ProductRepository;
import ecommerce.weariva.weariva_ecommerce.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class InitialDataSeeder {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, ProductRepository productRepository) {
        return args -> {

            // --- USERS ---
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setName("Admin");
                admin.setUsername("admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(this.passwordEncoder.encode("admin123")); // = no encoding (for demo only)
                admin.setRoles("ADMIN");
                admin.setCity("Xyz City");
                admin.setGender("male");
                admin.setPin("440085");
                admin.setPhone("8888888888");
                admin.setState("ABC State");
                admin.setUserActive(true);
                admin.setAccountNonLocked(true);
                admin.setCreatedAt(LocalDateTime.now());

                User user = new User();
                user.setName("John Doe");
                user.setUsername("johndoe");
                user.setEmail("user@gmail.com");
                user.setPassword(this.passwordEncoder.encode("user123"));
                user.setPhone("7777777777");
                user.setRoles("USER");
                user.setCity("XYZ City");
                user.setGender("male");
                user.setPin("440084");
                user.setState("ABC State");
                user.setUserActive(true);
                user.setFailedCount(0);
                user.setUserLockedTime(null);
                user.setAccountNonLocked(true);
                user.setCreatedAt(LocalDateTime.now());

                User delivery = new User();
                delivery.setName("Delivery Boy");
                delivery.setUsername("deliveryboy");
                delivery.setPhone("9999999999");
                delivery.setEmail("delivery@gmail.com");
                delivery.setPassword(this.passwordEncoder.encode("delivery123"));
                delivery.setRoles("DELIVERY");
                delivery.setCity("XYZ City");
                delivery.setGender("male");
                delivery.setPin("440084");
                delivery.setState("ABC State");
                delivery.setUserActive(true);
                delivery.setFailedCount(0);
                delivery.setUserLockedTime(null);
                delivery.setAccountNonLocked(true);
                delivery.setCreatedAt(LocalDateTime.now());

                userRepository.saveAll(List.of(admin, user, delivery));
                System.out.println("Default users created.");
            }

            // --- PRODUCTS ---
            if (productRepository.count() == 0) {
                Product p1 = new Product();
                p1.setName("Casual T-Shirt");
                p1.setCategory("Clothing");
                p1.setSubcategory("Men");
                p1.setPrice(599.0);
                p1.setDiscount(10);
                p1.setDiscountedPrice(539.0);
                p1.setStock(50);
                p1.setSizes(List.of("S", "L"));
                p1.setRating(List.of(new Rating("John Doe",
                        4,
                        "Great product overall, but delivery took an extra day.")));
                p1.setDescription("Soft cotton t-shirt for everyday wear.");
                p1.setImageUrl("/images/products/tshirt1.jpg");
                p1.setAvailable(true);

                Product p2 = new Product();
                p2.setName("Bluetooth Headphones");
                p2.setCategory("Electronics");
                p2.setSubcategory("Audio");
                p2.setPrice(1499.0);
                p2.setDiscount(15);
                p2.setDiscountedPrice(1274.0);
                p2.setRating(List.of(new Rating("John Doe",
                        5,
                        "Absolutely loved the quality! The fabric feels premium and fits perfectly.")));
                p2.setStock(30);
                p2.setDescription("Wireless headphones with noise cancellation.");
                p2.setImageUrl("/images/products/headphones1.jpg");
                p2.setAvailable(true);

                productRepository.saveAll(List.of(p1, p2));
                System.out.println("Default products created.");
            }
        };
    }
}
