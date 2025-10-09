package ecommerce.weariva.weariva_ecommerce.controllers;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ecommerce.weariva.weariva_ecommerce.enums.MyOrdersStatus;
import ecommerce.weariva.weariva_ecommerce.enums.PaymentStatus;
import ecommerce.weariva.weariva_ecommerce.models.OrderItems;
import ecommerce.weariva.weariva_ecommerce.models.Orders;
import ecommerce.weariva.weariva_ecommerce.models.Product;
import ecommerce.weariva.weariva_ecommerce.models.User;
import ecommerce.weariva.weariva_ecommerce.services.CartService;
import ecommerce.weariva.weariva_ecommerce.services.OrderService;
import ecommerce.weariva.weariva_ecommerce.services.ProductService;
import ecommerce.weariva.weariva_ecommerce.services.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final UserService userService;
    private final CartService cartService;
    private final OrderService orderService;

    @ModelAttribute
    public void getCurrentUserInfo(Model model) {
        User user = this.userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElse(null);
        if (!ObjectUtils.isEmpty(user)) {
            model.addAttribute("authenticatedUser", user);
            model.addAttribute("cartCount", this.cartService.cartCount(user));
        }
    }

    @GetMapping("")
    public String adminIndex(Model model) {

        List<Product> allProducts = this.productService.getAllProducts();

        Map<String, Long> values = allProducts.stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

        model.addAttribute("categoryNames",
                values.keySet().stream().limit(5).toList());
        model.addAttribute("categoryValues",
                values.values().stream().limit(5).toList());

        List<Orders> allOrders = this.orderService.getAllOrders();
        model.addAttribute("orders", allOrders.size());

        Map<String, Double> collect = allOrders.stream().flatMap(orderProducts -> orderProducts.getItems().stream())
                .toList().stream().collect(Collectors.groupingBy(OrderItems::getProductCategory,
                        Collectors.summingDouble(item -> item.getQuantity() * item.getDiscountedPrice())));

        model.addAttribute("salesKeys", collect.keySet());
        model.addAttribute("salesValues", collect.values());

        double revenue = allOrders.stream()
                .filter(item -> item.getPaymentStatus().toString().equals(PaymentStatus.PAID.toString()))
                .mapToDouble(item -> item.getTotalOrderAmount()).sum();
        model.addAttribute("revenue", revenue);

        double outstanding = allOrders.stream()
                .filter(item -> !item.getPaymentStatus().toString().equals(PaymentStatus.PAID.toString())
                        && !item.getOrderStatus().toString().equals(MyOrdersStatus.CANCELLED.toString()))
                .mapToDouble(item -> item.getTotalOrderAmount()).sum();
        model.addAttribute("outstanding", outstanding);

        Integer sales = allOrders.stream().filter(item -> item.getOrderStatus().equals(MyOrdersStatus.DELIVERED))
                .map(item -> item.getItems().stream().map(a -> a.getQuantity()).reduce(0, (a,
                        b) -> a + b))
                .toList().stream().reduce(0, (a, b) -> a + b);

        model.addAttribute("sales", sales);

        Map<MyOrdersStatus, Long> orderDetails = allOrders.stream()
                .collect(Collectors.groupingBy(Orders::getOrderStatus, Collectors.counting()));

        System.out.println(orderDetails);
        model.addAttribute("orderDetails", orderDetails);

        model.addAttribute("products", allProducts);

        return "/admin/index";
    }

    @GetMapping("/deliveryboys")
    public String deliveryboys(Model model) {
        model.addAttribute("deliveryboys", this.userService.getAllUserByRoles("DELIVERY"));
        return "/admin/deliveryboys";

    }

    @GetMapping("/addproduct")
    public String adminAddProducts(Model model, Principal principal) {
        model.addAttribute("uniqueid", UUID.randomUUID().toString());
        model.addAttribute("product", new Product());
        return "/admin/adminAddProduct";
    }

    @GetMapping("/allproducts")
    public String getAllProducts(Model model) {
        List<Product> allProducts = this.productService.getAllProducts();
        model.addAttribute("products", allProducts);
        model.addAttribute("productCount", allProducts.size());
        return "/admin/adminAllProducts";
    }

    @GetMapping("/editproduct/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        model.addAttribute("uniqueid", UUID.randomUUID().toString());
        model.addAttribute("editProducts", this.productService.getProductById(id));
        return "/admin/adminEditProduct";
    }

    @GetMapping("/addadmin")
    public String addAdmin(Model model) {
        List<User> allAdmins = this.userService.getAllUserByRoles("ADMIN");
        model.addAttribute("admindetails", allAdmins);
        return "/admin/addAdmin";
    }

    @GetMapping("/allorders")
    public String getAllOrders(@RequestParam(defaultValue = "") MyOrdersStatus category, Model model) {

        List<Orders> allOrders = null;
        if (ObjectUtils.isEmpty(category)) {
            allOrders = this.orderService.getAllOrders().stream()
                    .sorted((a, b) -> b.getOrderId().compareTo(a.getOrderId())).toList();
            model.addAttribute("paramvalue", "");
        } else {
            allOrders = this.orderService.getOrdersByStatus(category).stream()
                    .sorted((a, b) -> b.getOrderId().compareTo(a.getOrderId())).toList();
            model.addAttribute("paramvalue", category);
        }

        List<User> deliveryboys = this.userService.getAllUserByRoles("DELIVERY").stream()
                .filter(item -> item.isUserActive()).toList();
        List<PaymentStatus> paymentList = Arrays.stream(PaymentStatus.values()).toList();

        model.addAttribute("allOrders", allOrders);
        model.addAttribute("alldelivery", deliveryboys);
        model.addAttribute("paymentList", paymentList);

        return "admin/allOrders";
    }

    @GetMapping("/order-transactions")
    public String orderTransactionView(Model model) {

        model.addAttribute("orderTransactions", this.orderService.getAllOrders());
        return "/ordertransactions";
    }

    @GetMapping("/view-order/{id}")
    public String orderView(@PathVariable Long id, Model model) {
        model.addAttribute("order", this.orderService.getMyOrderById(id).orElse(null));
        return "/viewinvoice";
    }

    @GetMapping("/profiledetails")
    public String getMethodName() {
        return "/admin/adminprofile";
    }

    @GetMapping("/editprofile")
    public String editProfile() {
        return "admin/editProfile";
    }

    @GetMapping("signout")
    public String signout() {
        return "logout";
    }

}
