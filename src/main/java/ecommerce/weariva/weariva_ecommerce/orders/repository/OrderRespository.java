package ecommerce.weariva.weariva_ecommerce.orders.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecommerce.weariva.weariva_ecommerce.orders.enums.MyOrdersStatus;
import ecommerce.weariva.weariva_ecommerce.orders.models.Orders;
import ecommerce.weariva.weariva_ecommerce.user.models.User;

@Repository
public interface OrderRespository extends JpaRepository<Orders, Long> {

    List<Orders> findByUser(User user);

    List<Orders> findByOrderStatus(MyOrdersStatus orderStatus);

    List<Orders> findByPaymentStatus(String paymentStatus);
}

