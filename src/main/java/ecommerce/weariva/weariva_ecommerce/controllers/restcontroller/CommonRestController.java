package ecommerce.weariva.weariva_ecommerce.controllers.restcontroller;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ecommerce.weariva.weariva_ecommerce.enums.MyOrdersStatus;
import ecommerce.weariva.weariva_ecommerce.enums.PaymentStatus;
import ecommerce.weariva.weariva_ecommerce.models.Orders;
import ecommerce.weariva.weariva_ecommerce.records.RestApiResponse;
import ecommerce.weariva.weariva_ecommerce.services.OrderService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/commonadminboy")
@RequiredArgsConstructor
public class CommonRestController {

    private final OrderService orderService;

    @PostMapping("paymentupdate")
    public ResponseEntity<?> paymentStatus(@RequestParam Long orderId,
            @RequestParam String paymentWay,
            @RequestParam(required = false) String transactionId,
             @RequestParam PaymentStatus paymentStatus) {
        try {
            Orders order = this.orderService.getMyOrderById(orderId).orElse(null);
            if (!ObjectUtils.isEmpty(order)) {

                try {
                    order.setPaymentStatus(paymentStatus);
                    this.orderService.saveOrder(order);
                    return ResponseEntity.ok()
                            .body(new RestApiResponse(true, "Successfully Paid"));
                } catch (Exception e) {
                    order.setPaymentStatus(PaymentStatus.PAYMENT_FAILED);
                    this.orderService.saveOrder(order);
                    throw new Exception();
                }

            } else {
                return ResponseEntity.ok().body(new RestApiResponse(false, "Order not Found"));
            }
        } catch (Exception e) {

            return ResponseEntity.ok().body(new RestApiResponse(false, "Payment Failed."));
        }

    }

    @PostMapping("/changestatus")
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
