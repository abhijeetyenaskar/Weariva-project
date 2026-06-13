package ecommerce.weariva.weariva_ecommerce.orders.restcontrollers;

import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ecommerce.weariva.weariva_ecommerce.common.records.RestApiResponse;
import ecommerce.weariva.weariva_ecommerce.orders.enums.MyOrdersStatus;
import ecommerce.weariva.weariva_ecommerce.orders.models.Orders;
import ecommerce.weariva.weariva_ecommerce.orders.services.OrderService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class OrderRestController {

    private final OrderService orderService;

    
    @PostMapping("/admin/addordertodelivery")
    public ResponseEntity<?> addDeliveryOrder(@RequestParam String username, @RequestParam Long orderId) {
        try {
            Orders order = this.orderService.getMyOrderById(orderId).orElse(null);
            if (!ObjectUtils.isEmpty(order)) {
                order.setDeliveryAgent(username);
                this.orderService.saveOrder(order);
                return ResponseEntity.ok().body(new RestApiResponse(true, "Successfully Alloted to " + username));
            } else {
                return ResponseEntity.ok().body(new RestApiResponse(false, "Order not Found"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body(new RestApiResponse(false, "Internal Server Error"));
        }

    }

    @PostMapping("/commonadminboy/changestatus")
    public ResponseEntity<?> changeOrderStatus(@RequestParam String status, @RequestParam Long orderId) {

        try {

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
            return ResponseEntity.ok().body(new RestApiResponse(true, status));

        } catch (Exception e) {
            return ResponseEntity.ok().body(new RestApiResponse(true, "Change Order Status failed"));
        }
    }
}
