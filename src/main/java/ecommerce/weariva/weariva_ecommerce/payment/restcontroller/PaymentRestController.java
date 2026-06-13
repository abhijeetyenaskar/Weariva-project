package ecommerce.weariva.weariva_ecommerce.payment.restcontroller;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ecommerce.weariva.weariva_ecommerce.common.records.RestApiResponse;
import ecommerce.weariva.weariva_ecommerce.orders.models.Orders;
import ecommerce.weariva.weariva_ecommerce.orders.services.OrderService;
import ecommerce.weariva.weariva_ecommerce.payment.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class PaymentRestController {

    private final OrderService orderService;

    @PostMapping("/commonadminboy/paymentupdate")
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

}
