package ecommerce.weariva.weariva_ecommerce.controllers;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ecommerce.weariva.weariva_ecommerce.models.Cart;
import ecommerce.weariva.weariva_ecommerce.models.Product;
import ecommerce.weariva.weariva_ecommerce.models.Rating;
import ecommerce.weariva.weariva_ecommerce.models.User;
import ecommerce.weariva.weariva_ecommerce.services.CartService;
import ecommerce.weariva.weariva_ecommerce.services.ProductService;
import ecommerce.weariva.weariva_ecommerce.services.UserService;
import ecommerce.weariva.weariva_ecommerce.services.messageservices.MessageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final ProductService productService;
    private final MessageService messageService;
    private final CartService cartService;
    private final PasswordEncoder passwordEncoder;

    @ModelAttribute
    public void getCurrentUserInfo(Model model) {
        User user = this.userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElse(null);
        if (!ObjectUtils.isEmpty(user)) {
            model.addAttribute("authenticatedUser", user);
            model.addAttribute("cartCount", this.cartService.cartCount(user));
        }
    }

    @GetMapping("/")
    public String getIndex(Model model) {
        List<Product> products = this.productService.getAllProducts().stream().limit(5).toList();
        List<String> categories = products.stream().map(item -> item.getCategory()).distinct().toList();

        List<Rating> rating = products.stream().flatMap(item -> item.getRating().stream()).toList().stream()
                .collect(Collectors.toMap(Rating::getUsername, r -> r, (existing, replacement) -> existing)).values().stream()
                .filter(rate -> rate.getRating() > 3)
                .limit(3).toList();
        model.addAttribute("products", products);
        model.addAttribute("ratings", rating);
        model.addAttribute("categories", categories);
        return "index";
    }

    @GetMapping("/signin")
    public String getLogin(Model model) {
        model.addAttribute("login", true);
        return "login";
    }

    @GetMapping("/signup")
    public String getSignUp(Model model) {
        model.addAttribute("signup", true);
        return "signup";
    }

    @PostMapping("/uploadsignup")
    public String uploadUser(@ModelAttribute User user,
            RedirectAttributes redirectAttribute) throws IOException {

        if (this.userService.existByCurrentUsername(user.getUsername())) {
            redirectAttribute.addFlashAttribute("duplicate", "User with same username is already Exists.");
            return "redirect:/signup";
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles("USER");
            user.setAccountNonLocked(true);
            user.setFailedCount(0);
            user.setUserActive(true);
            user.setUserLockedTime(null);
            User signupUserDB = this.userService.saveUser(user);
            if (!ObjectUtils.isEmpty(signupUserDB)) {
                redirectAttribute.addFlashAttribute("success", signupUserDB.getUsername() + " successfully SignedUp.");
                return "redirect:/signin";
            } else {
                redirectAttribute.addFlashAttribute("error", "Internal Server Error");
                return "redirect:/signup";
            }
        }
    }

    @GetMapping("/products")
    public String getProducts(@RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "") String subcategory, Model model) {
        List<Product> list = this.productService.getAllProducts();
        Stream<Product> stream = list.stream();

        if (category != "") {
            stream = stream.filter(item -> item.getCategory().equals(category));
        }

        if (subcategory != "") {
            stream = stream.filter(item -> item.getSubcategory().equals(subcategory));
        }

        List<Product> products = stream.toList();

        Map<String, Set<String>> categories = list.stream().collect(Collectors.groupingBy(a -> a.getCategory(),
                Collectors.mapping(Product::getSubcategory, Collectors.toSet())));

        model.addAttribute("products", products);
        model.addAttribute("paramvalue", category);
        model.addAttribute("categories", categories);
        return "products";
    }

    @GetMapping("/productdetails/{id}")
    public String getProductDetails(@PathVariable Long id, Model model) {

        Product productById = this.productService.getProductById(id);
        if (!ObjectUtils.isEmpty(productById)) {

            User user = this.userService
                    .getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElse(null);
            if (!ObjectUtils.isEmpty(user)) {
                Cart cart = this.cartService.getCartItemByUserAndProduct(user, productById).orElse(null);
                if (cart != null) {
                    model.addAttribute("cartItem", cart);
                }
            }
            model.addAttribute("product", productById);
            final List<Product> list = this.productService.getAllProductsByCategory(productById.getCategory()).stream()
                    .filter(item -> item.getName() != productById.getName()).limit(4).toList();
            model.addAttribute("productCategory", list);
            model.addAttribute("productsSize", list.size());
            model.addAttribute("rating", new Rating());

            if (productById.getRating().size() > 0) {
                Map<Integer, Long> collect = productById.getRating().stream()
                        .collect(Collectors.groupingBy(Rating::getRating, Collectors.counting()));
                model.addAttribute("productRatings", productById.getRating());
                model.addAttribute("ratingKeys", collect.keySet());
                model.addAttribute("ratingValues", collect.values());
            }

        } else {
            model.addAttribute("product", null);
        }

        return "productdetails";
    }

    @GetMapping("/unAuthenticated")
    public String unAuthenticated() {
        return "unauthenticated";
    }

    @GetMapping("/forgetpassword")
    public String forgetPassword(Model model) {
        model.addAttribute("forget", true);
        return "/forgetpassword";
    }

    @PostMapping("/confirmuser")
    public String resetPassword(@RequestParam String username, HttpSession session, Model model) {

        User user = this.userService.getUserByUsername(username).orElse(null);
        System.out.println(user);
        if (ObjectUtils.isEmpty(user)) {
            session.setAttribute("error", "Invalid Username!");
            return "forgetpassword";
        } else {
            model.addAttribute("user", user);
            return "chooseotp";
        }
    }

    // method is used for creating six digit otp
    private String otpGenerator() {
        return new DecimalFormat("000000").format(new Random().nextInt(999999));
    }

    @PostMapping("/sendOTP")
    public String sentOTP(@RequestParam String choose, Model model, HttpSession session) {

        model.addAttribute("choose", choose);
        if (choose.contains("@")) {
            User user = this.userService.getUserByEmail(choose).orElse(null);
            if (!ObjectUtils.isEmpty(user)) {
                String otp = otpGenerator();
                user.setPasswordResetToken(otp);
                User otpsend = userService.saveUser(user);

                String linkMessage = "<h4>OTP is " + otp + "</h4>";
                System.out.println(linkMessage);
                messageService.sendEmail(choose, "OTP from Weariva Ecommerce Application!!", linkMessage);
                model.addAttribute("username", otpsend.getUsername());
            }
            model.addAttribute("type", "Email");
            return "otpvalidation";
        } else {
            model.addAttribute("type", "Phone");
            session.setAttribute("wrongotp", "Phone OTP is not working!!!");
            return "forgetpassword";
        }
    }

    @PostMapping("/varifyOtp")
    public String varifyOTP(@RequestParam String username, @RequestParam String otp, Model model, HttpSession session) {

        User user = this.userService.getUserByUsername(username).orElse(null);
        if (!ObjectUtils.isEmpty(user) && (otp.equals(user.getPasswordResetToken()))) {
            model.addAttribute("username", user.getUsername());
            return "/changepassword";
        } else {
            session.setAttribute("wrongotp", "Wrong OTP!! try it again.");
            return "redirect:/forgetpassword";
        }

    }

    @PostMapping("/resetPassword")
    public String changedPassword(@RequestParam String username, @RequestParam String newpassword, Model model,
            HttpSession session) {

        User user = this.userService.getUserByUsername(username).orElse(null);
        if (!ObjectUtils.isEmpty(user)) {
            user.setPassword(passwordEncoder.encode(newpassword));
            user.setPasswordResetToken(null);
            userService.saveUser(user);
            return "/login";
        } else {
            session.setAttribute("wrongotp", "Wrong OTP!! try it again.");
            return "redirect:/forgetpassword";
        }

    }

}
