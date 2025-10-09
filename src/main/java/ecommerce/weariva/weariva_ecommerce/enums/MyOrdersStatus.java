package ecommerce.weariva.weariva_ecommerce.enums;

public enum MyOrdersStatus {

    PENDING("Pending"),
    CONFIRM("Confirm"),
    PROCESSING("Processing"),
    DISPATCHED("Dispatched"),
    OUT_FOR_DELIVERY("OutForDelivery"),
    DELIVERED("Delivered"),

    CANCELLED("Cancelled"),
    REJECTED("Rejected"),
    FAILED("Failed"),

    RETURN_REQUESTED("ReturnRequested"),
    RETURNED("Returned");
    
    private String name;

    private MyOrdersStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
