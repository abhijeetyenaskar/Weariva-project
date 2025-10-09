package ecommerce.weariva.weariva_ecommerce.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecommerce.weariva.weariva_ecommerce.enums.MyOrdersStatus;
import ecommerce.weariva.weariva_ecommerce.models.Orders;
import ecommerce.weariva.weariva_ecommerce.models.User;

@Repository
public interface OrderRespository extends JpaRepository<Orders, Long> {

    List<Orders> findByUser(User user);

    List<Orders> findByOrderStatus(MyOrdersStatus orderStatus);

    List<Orders> findByPaymentStatus(String paymentStatus);
}

