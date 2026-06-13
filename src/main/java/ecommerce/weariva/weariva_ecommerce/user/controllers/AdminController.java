package ecommerce.weariva.weariva_ecommerce.user.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ecommerce.weariva.weariva_ecommerce.orders.enums.MyOrdersStatus;
import ecommerce.weariva.weariva_ecommerce.orders.models.OrderItems;
import ecommerce.weariva.weariva_ecommerce.orders.models.Orders;
import ecommerce.weariva.weariva_ecommerce.orders.services.OrderService;
import ecommerce.weariva.weariva_ecommerce.payment.enums.PaymentStatus;
import ecommerce.weariva.weariva_ecommerce.product.models.Product;
import ecommerce.weariva.weariva_ecommerce.product.services.ProductService;
import ecommerce.weariva.weariva_ecommerce.user.models.User;
import ecommerce.weariva.weariva_ecommerce.user.services.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;

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

        model.addAttribute("orderDetails", orderDetails);

        model.addAttribute("products", allProducts);

        return "/admin/index";
    }

    @GetMapping("/deliveryboys")
    public String deliveryboys(Model model) {
        model.addAttribute("deliveryboys", this.userService.getAllUserByRoles("DELIVERY"));
        return "/admin/deliveryboys";

    }

    @GetMapping("/addadmin")
    public String addAdmin(Model model) {
        List<User> allAdmins = this.userService.getAllUserByRoles("ADMIN");
        model.addAttribute("admindetails", allAdmins);
        return "/admin/addAdmin";
    }

    @GetMapping("/profiledetails")
    public String profileDetails() {
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
