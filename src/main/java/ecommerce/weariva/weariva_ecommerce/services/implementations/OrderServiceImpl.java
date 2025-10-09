package ecommerce.weariva.weariva_ecommerce.services.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ecommerce.weariva.weariva_ecommerce.enums.MyOrdersStatus;
import ecommerce.weariva.weariva_ecommerce.models.Orders;
import ecommerce.weariva.weariva_ecommerce.models.User;
import ecommerce.weariva.weariva_ecommerce.repositories.OrderRespository;
import ecommerce.weariva.weariva_ecommerce.services.OrderService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRespository orderRespository;

    @Override
    public List<Orders> getAllOrdersByUser(User user) {
        return this.orderRespository.findByUser(user);
    }

    @Override
    public List<Orders> getAllOrders() {
        return this.orderRespository.findAll();
    }

    @Override
    public Optional<Orders> getMyOrderById(Long orderId) {
        return this.orderRespository.findById(orderId);
    }

    @Override
    public Orders saveOrder(Orders myOrders) {
        return this.orderRespository.save(myOrders);
    }

    @Override
    public List<Orders> getOrdersByStatus(MyOrdersStatus category) {
        return this.orderRespository.findByOrderStatus(category);
    }

}
