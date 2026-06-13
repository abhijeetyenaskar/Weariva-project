package ecommerce.weariva.weariva_ecommerce.user.restcontrollers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ecommerce.weariva.weariva_ecommerce.cart.models.Cart;
import ecommerce.weariva.weariva_ecommerce.cart.services.CartService;
import ecommerce.weariva.weariva_ecommerce.common.records.RestApiResponse;
import ecommerce.weariva.weariva_ecommerce.common.sseservices.SseService;
import ecommerce.weariva.weariva_ecommerce.orders.dtos.PlaceOrderRequest;
import ecommerce.weariva.weariva_ecommerce.orders.enums.MyOrdersStatus;
import ecommerce.weariva.weariva_ecommerce.orders.models.OrderAddress;
import ecommerce.weariva.weariva_ecommerce.orders.models.OrderItems;
import ecommerce.weariva.weariva_ecommerce.orders.models.Orders;
import ecommerce.weariva.weariva_ecommerce.orders.services.OrderService;
import ecommerce.weariva.weariva_ecommerce.payment.enums.PaymentStatus;
import ecommerce.weariva.weariva_ecommerce.product.models.Product;
import ecommerce.weariva.weariva_ecommerce.product.services.ProductService;
import ecommerce.weariva.weariva_ecommerce.user.dtos.UpdateUser;
import ecommerce.weariva.weariva_ecommerce.user.models.User;
import ecommerce.weariva.weariva_ecommerce.user.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserRestController {

    private final UserService userService;
    private final CartService cartService;
    private final OrderService orderService;
    private final SseService sseService;
    private final ProductService productService;

    @PostMapping("/place-order")
    public ResponseEntity<?> placeOrder(@RequestBody PlaceOrderRequest placeOrderRequest) {
        try {

            User currentUser = this.userService
                    .getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElse(null);
            List<Cart> cartItems = this.cartService.getAllCartItemsByUser(currentUser);
            Orders orders = Orders.builder().user(currentUser)
                    .orderAddress(OrderAddress.builder().street(placeOrderRequest.getStreet())
                            .city(placeOrderRequest.getCity()).state(placeOrderRequest.getState())
                            .country(placeOrderRequest.getCountry()).phone(placeOrderRequest.getPhone()).build())
                    .orderDate(LocalDateTime.now())
                    .build();
            List<OrderItems> orderItems = cartItems.stream().map(cart -> {
                OrderItems orderItem = new OrderItems();
                orderItem.setDiscount(cart.getProduct().getDiscount());
                orderItem.setOrder(orders);
                orderItem.setQuantity(cart.getQuantity());
                orderItem.setProductName(cart.getProduct().getName());
                orderItem.setProductCategory(cart.getProduct().getCategory());
                orderItem.setProductImage(cart.getProduct().getImageUrl());
                orderItem.setPrice(cart.getProduct().getPrice());
                orderItem.setDiscountedPrice(cart.getProduct().getDiscountedPrice());
                return orderItem;
            }).toList();
            orders.setItems(orderItems);
            orders.setTotalOrderAmount(
                    orderItems.stream().mapToDouble(value -> value.getQuantity() * value.getDiscountedPrice()).sum()
                            + 20);

            orders.setPaymentMode(placeOrderRequest.getPaymentmode());
            orders.setPaymentStatus(PaymentStatus.PAYMENT_AWAITING);
            orders.setOrderStatus(MyOrdersStatus.PENDING);
            Orders saveOrder = this.orderService.saveOrder(orders);

            this.sseService.sendNewOrderNotification(saveOrder.getOrderId() + "");

            cartItems.stream().forEach(item -> {
                Product productById = this.productService.getProductById(item.getProduct().getProductId());
                productById.setStock(productById.getStock() - item.getQuantity());
                this.productService.saveProduct(productById);
            });
            this.cartService.deleteCartItems(cartItems);
            return ResponseEntity.ok().body(new RestApiResponse(true, "Successfully Order Placed"));

        } catch (Exception e) {
            return ResponseEntity.ok().body(new RestApiResponse(false, "Placing Order Failed"));
        }
    }

    @PostMapping("updateprofile")
    public ResponseEntity<?> updateProfile(@ModelAttribute UpdateUser updateUser) {

        try {
            User user = this.userService
                    .getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                    .orElse(null);
            if (!org.springframework.util.ObjectUtils.isEmpty(user)) {
                user.setName(updateUser.getName());
                user.setUsername(updateUser.getUsername());
                user.setEmail(updateUser.getEmail());
                user.setPhone(updateUser.getPhone());
                user.setGender(updateUser.getGender());
                user.setCity(updateUser.getCity());
                user.setState(updateUser.getState());
                user.setPin(updateUser.getPin());
                this.userService.saveUser(user);
                return ResponseEntity.ok().body(new RestApiResponse(true, "Profile Successfully Updated!!"));
            } else {
                return ResponseEntity.ok().body(new RestApiResponse(false, "No User Found"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body(new RestApiResponse(false, "Internal Server Error"));
        }
    }

}
