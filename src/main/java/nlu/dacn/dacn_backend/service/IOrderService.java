package nlu.dacn.dacn_backend.service;

import nlu.dacn.dacn_backend.dto.response.OrderResponse;
import nlu.dacn.dacn_backend.enumv1.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IOrderService {
    void orderLaptop(String token);
    OrderResponse getOrder(String token, Long orderId);
    List<OrderResponse> getOrders(String token);
    OrderResponse updateOrderStatus(String token, Long orderId, OrderStatus orderStatus);
}
