package ecommerce.weariva.weariva_ecommerce.models;

import java.time.LocalDateTime;
import java.util.List;

import ecommerce.weariva.weariva_ecommerce.enums.MyOrdersStatus;
import ecommerce.weariva.weariva_ecommerce.enums.PaymentStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderId;

    private String deliveryAgent;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private MyOrdersStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    private List<OrderItems> items;

    private String paymentMode;

    @ManyToOne
    private User user;

    private double totalOrderAmount;

    @Embedded
    private OrderAddress orderAddress;

}
