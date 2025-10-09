package ecommerce.weariva.weariva_ecommerce.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ecommerce.weariva.weariva_ecommerce.enums.MyOrdersStatus;
import ecommerce.weariva.weariva_ecommerce.models.Orders;
import ecommerce.weariva.weariva_ecommerce.models.User;

@Service
public interface OrderService {

    public List<Orders> getAllOrdersByUser(User user);

    public Optional<Orders> getMyOrderById(Long orderId);

    public Orders saveOrder(Orders myOrders);

    public List<Orders> getAllOrders();

    public List<Orders> getOrdersByStatus(MyOrdersStatus category);
}
