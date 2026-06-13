package ecommerce.weariva.weariva_ecommerce.orders.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ecommerce.weariva.weariva_ecommerce.orders.enums.MyOrdersStatus;
import ecommerce.weariva.weariva_ecommerce.orders.models.Orders;
import ecommerce.weariva.weariva_ecommerce.user.models.User;

@Service
public interface OrderService {

    public List<Orders> getAllOrdersByUser(User user);

    public Optional<Orders> getMyOrderById(Long orderId);

    public Orders saveOrder(Orders myOrders);

    public List<Orders> getAllOrders();

    public List<Orders> getOrdersByStatus(MyOrdersStatus category);
}
