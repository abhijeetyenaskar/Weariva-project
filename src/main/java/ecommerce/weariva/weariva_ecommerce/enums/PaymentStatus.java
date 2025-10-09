package ecommerce.weariva.weariva_ecommerce.enums;

public enum PaymentStatus {

    PAYMENT_AWAITING("Awaiting"),
    PAID("Paid"),
    PAYMENT_FAILED("PaymentFailed"),
    REFUND_INITIATED("RefundInitiated"),
    REFUNDED("Refunded");

    private String paymentName;

    private PaymentStatus(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getName() {
        return this.paymentName;
    }
}
