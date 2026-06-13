package ecommerce.weariva.weariva_ecommerce.product.controllers;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import ecommerce.weariva.weariva_ecommerce.product.models.Product;
import ecommerce.weariva.weariva_ecommerce.product.models.Rating;
import ecommerce.weariva.weariva_ecommerce.product.services.ProductService;
import ecommerce.weariva.weariva_ecommerce.user.models.User;
import ecommerce.weariva.weariva_ecommerce.user.services.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    @GetMapping("/admin/addproduct")
    public String adminAddProducts(Model model, Principal principal) {
        model.addAttribute("uniqueid", UUID.randomUUID().toString());
        model.addAttribute("product", new Product());
        return "/admin/adminAddProduct";
    }

    @GetMapping("/admin/allproducts")
    public String getAllProducts(Model model) {
        List<Product> allProducts = this.productService.getAllProducts();
        model.addAttribute("products", allProducts);
        model.addAttribute("productCount", allProducts.size());
        return "/admin/adminAllProducts";
    }

    @GetMapping("/admin/editproduct/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        model.addAttribute("uniqueid", UUID.randomUUID().toString());
        model.addAttribute("editProducts", this.productService.getProductById(id));
        return "/admin/adminEditProduct";
    }

    @PostMapping("/user/rating/{id}")
    public String setRating(@ModelAttribute Rating rating, @PathVariable Long id, Model model, Principal principal) {

        User user = this.userService.getUserByUsername(principal.getName()).orElse(null);
        if (user != null) {
            Product productById = this.productService.getProductById(id);
            if (!ObjectUtils.isEmpty(productById)) {
                productById.getRating().add(
                        new Rating(user.getName(),
                                rating.getRating(),
                                rating.getReview()));
                this.productService.saveProduct(productById);
            }
        }

        return "redirect:/productdetails/" + id;
    }

}
