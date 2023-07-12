package nlu.dacn.dacn_backend.controller;

import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.dto.request.OrderDTO;
import nlu.dacn.dacn_backend.dto.response.OrderResponse;
import nlu.dacn.dacn_backend.dto.response.ResponMessenger;
import nlu.dacn.dacn_backend.service.impl.OrderService;
import nlu.dacn.dacn_backend.utils.TokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<?> addLaptopToCart(@RequestHeader("Authorization") String authHeader) {
        String token = TokenUtils.getTokenFromHeader(authHeader);
        orderService.orderLaptop(token);
        return new ResponseEntity<>(new ResponMessenger("Đã đặt đơn hàng"), HttpStatus.OK);
    }

    @GetMapping("/order/list")
    public List<OrderResponse> getOrders(@RequestHeader("Authorization") String authHeader) {
        String token = TokenUtils.getTokenFromHeader(authHeader);
        return orderService.getOrders(token);
    }

    @GetMapping("/order/detail")
    public OrderResponse getOrder(@RequestParam Long orderId, @RequestHeader("Authorization") String authHeader) {
        String token = TokenUtils.getTokenFromHeader(authHeader);
        return orderService.getOrder(token, orderId);
    }

    @PutMapping("/order/update")
    public OrderResponse updateOrderStatus(@RequestBody OrderDTO orderDTO, @RequestHeader("Authorization") String authHeader) {
        String token = TokenUtils.getTokenFromHeader(authHeader);
        orderDTO.setToken(token);
        return orderService.updateOrderStatus(orderDTO.getToken(), orderDTO.getOrderId(), orderDTO.getOrderStatus());
    }

}
