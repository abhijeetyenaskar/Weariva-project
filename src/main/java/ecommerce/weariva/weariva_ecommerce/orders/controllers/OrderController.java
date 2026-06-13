package ecommerce.weariva.weariva_ecommerce.orders.controllers;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import ecommerce.weariva.weariva_ecommerce.orders.enums.MyOrdersStatus;
import ecommerce.weariva.weariva_ecommerce.orders.models.Orders;
import ecommerce.weariva.weariva_ecommerce.orders.services.OrderService;
import ecommerce.weariva.weariva_ecommerce.payment.enums.PaymentStatus;
import ecommerce.weariva.weariva_ecommerce.user.models.User;
import ecommerce.weariva.weariva_ecommerce.user.services.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("/admin/allorders")
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

    @GetMapping("/admin/order-transactions")
    public String orderTransactionView(Model model) {

        model.addAttribute("orderTransactions", this.orderService.getAllOrders());
        return "/ordertransactions";
    }

    @GetMapping("/{role}/view-order/{id}")
    public String orderView(@PathVariable Long id, Model model) {

        model.addAttribute("order", this.orderService.getMyOrderById(id).orElse(null));
        return "/viewinvoice";
    }

    @GetMapping("/user/view-order-transactions")
    public String viewOrderTransactions(Model model, Principal principal) {
        List<Orders> userOrders = this.orderService
                .getAllOrdersByUser(this.userService.getUserByUsername(principal.getName()).orElse(null));
        model.addAttribute("orderTransactions", userOrders);
        return "/ordertransactions";
    }

    @GetMapping("/user/myorders")
    public String getMyOrders(Model model, Principal principal) {
        User user = this.userService.getUserByUsername(principal.getName()).orElse(null);
        if (!ObjectUtils.isEmpty(user)) {
            List<Orders> allOrdersByUser = this.orderService.getAllOrdersByUser(user);
            allOrdersByUser = allOrdersByUser.stream().sorted((a, b) -> b.getOrderId().compareTo(a.getOrderId()))
                    .toList();

            model.addAttribute("allOrdersSize", allOrdersByUser.size());
            model.addAttribute("allOrders", allOrdersByUser);
        } else {
            model.addAttribute("allOrdersSize", 0);
        }
        return "/user/myorders";
    }

    @GetMapping("/user/updatstatus/{status}/{orderId}")
    public String getUpdateState(@PathVariable String status, @PathVariable Long orderId) {

        Orders myorder = this.orderService.getMyOrderById(orderId).orElse(null);
        if (!ObjectUtils.isEmpty(myorder)) {
            for (MyOrdersStatus changeStatus : MyOrdersStatus.values()) {
                if (status.equals(changeStatus.toString())) {
                    myorder.setOrderStatus(changeStatus);
                    break;
                }
            }
            this.orderService.saveOrder(myorder);

        }
        return "redirect:/user/myorders";
    }

    @GetMapping("/boy/myorders")
    public String boySpecifiedOrders(Model model) {

        List<Orders> deliveryBoyOrderList = this.orderService.getAllOrders().stream()
                .filter(item -> item.getDeliveryAgent() == null || item
                        .getDeliveryAgent().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
                .toList();
        model.addAttribute("allDeliveryOrders", deliveryBoyOrderList.size());

        List<Orders> list = deliveryBoyOrderList.stream()
                .filter(item -> item.getOrderStatus().toString().equals("OUT_FOR_DELIVERY")).toList();
        model.addAttribute("deliveryOrders", list);
        return "/boys/myorders";
    }

}
