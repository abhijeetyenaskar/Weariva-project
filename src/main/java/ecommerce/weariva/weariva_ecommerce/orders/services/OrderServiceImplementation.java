package ecommerce.weariva.weariva_ecommerce.orders.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ecommerce.weariva.weariva_ecommerce.orders.enums.MyOrdersStatus;
import ecommerce.weariva.weariva_ecommerce.orders.models.Orders;
import ecommerce.weariva.weariva_ecommerce.orders.repository.OrderRespository;
import ecommerce.weariva.weariva_ecommerce.user.models.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImplementation implements OrderService {

    private final OrderRespository orderRespository;

    @Override
    public List<Orders> getAllOrdersByUser(User user) {
        return this.orderRespository.findByUser(user);
    }

    @Override
    public List<Orders> getAllOrders() {
        return this.orderRespository.findAll();
    }

    @SuppressWarnings("null")
    @Override
    public Optional<Orders> getMyOrderById(Long orderId) {
        return this.orderRespository.findById(orderId);
    }

    @SuppressWarnings("null")
    @Override
    public Orders saveOrder(Orders myOrders) {
        return this.orderRespository.save(myOrders);
    }

    @Override
    public List<Orders> getOrdersByStatus(MyOrdersStatus category) {
        return this.orderRespository.findByOrderStatus(category);
    }

}
