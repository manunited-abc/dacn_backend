package nlu.dacn.dacn_backend.service.impl;

import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.converter.LaptopConverter;
import nlu.dacn.dacn_backend.dto.request.LaptopDTO;
import nlu.dacn.dacn_backend.dto.response.OrderResponse;
import nlu.dacn.dacn_backend.entity.Account;
import nlu.dacn.dacn_backend.entity.Cart;
import nlu.dacn.dacn_backend.entity.Laptop;
import nlu.dacn.dacn_backend.entity.OrderTest;
import nlu.dacn.dacn_backend.enumv1.OrderStatus;
import nlu.dacn.dacn_backend.exception.ServiceException;
import nlu.dacn.dacn_backend.repository.AccountRepository;
import nlu.dacn.dacn_backend.repository.OrderRepository;
import nlu.dacn.dacn_backend.security.jwt.JwtTokenProvider;
import nlu.dacn.dacn_backend.service.IOrderService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final JwtTokenProvider jwtTokenProvider;
    private final LaptopConverter laptopConverter;
    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;


    @Override
    public void orderLaptop(String token) {
        Account account = getAccountFromToken(token);
        Cart cart = account.getCart();
        if (cart.getCartLaptop().isEmpty()) {
            throw new ServiceException("Giỏ hàng trống, vui lòng thêm đơn hàng trước khi đặt");
        }
        OrderTest order = new OrderTest();
        order.setAccount(account);
        order.setOrderedLaptops(new HashMap<>(cart.getCartLaptop()));
        order.setOrderStatus(OrderStatus.PENDING);
        cart.clearItems();
        account.getOrderTests().add(order);
        accountRepository.save(account);
    }

    @Override
    public OrderResponse getOrder(String token, Long orderId) {
        getAccountFromToken(token);
        Optional<OrderTest> optional = orderRepository.findById(orderId);
        if (optional.isEmpty()) {
            throw new ServiceException("Không tìm thấy đơn hàng yêu cầu");
        }
        OrderTest order = optional.get();
        return toOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getOrders(String token) {
        Account account = getAccountFromToken(token);
        if (account.getOrderTests().isEmpty()) {
            throw new ServiceException("Tài khoản của bạn chưa có đơn hàng nào");
        }
        List<OrderResponse> orderResponseList = new ArrayList<>();
        List<OrderTest> orders = account.getOrderTests();

        OrderResponse orderResponse;
        for (OrderTest order : orders) {
            orderResponse = toOrderResponse(order);
            orderResponseList.add(orderResponse);
        }

        return orderResponseList;
    }

    @Override
    public OrderResponse updateOrderStatus(String token, Long orderId, OrderStatus orderStatus) {
        Account account = getAccountFromToken(token);
        OrderResponse orderResponse = new OrderResponse();
        for (OrderTest order : account.getOrderTests()) {
            if (order.getId().equals(orderId)) {
                if (order.getOrderStatus() == OrderStatus.CANCELLED) {
                    throw new ServiceException("Đơn hàng đã huỷ,không thể cập nhật");
                }
                if (order.getOrderStatus() == OrderStatus.DELIVERED) {
                    throw new ServiceException("Đơn hàng đã giao, không thể cập nhật");
                }
                order.setOrderStatus(orderStatus);
                orderResponse = toOrderResponse(order);
            }
        }
        accountRepository.save(account);
        return orderResponse;
    }

    private Account getAccountFromToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new ServiceException("Hết phiên làm việc, vui lòng đăng nhập lại");
        }
        String username = jwtTokenProvider.getUserNameFromToken(token);
        Optional<Account> optionalAccount = accountRepository.findByUserName(username);
        if (optionalAccount.isEmpty()) {
            throw new ServiceException("Không tìm thấy tài khoản");
        }
        return optionalAccount.get();
    }

    private OrderResponse toOrderResponse(OrderTest order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setOrderDate(order.getCreatedDate());
        orderResponse.setStatus(order.getOrderStatus().getDescription());
        orderResponse.setTotalPayment(order.calculateTotalPrice());
        orderResponse.setAddressDetail(order.getAccount().getAddressDetail());

        LaptopDTO laptopDTO;
        for (Map.Entry<Laptop, Integer> entry : order.getOrderedLaptops().entrySet()) {
            laptopDTO = laptopConverter.toLaptopDTO(entry.getKey());
            laptopDTO.setQuantity(entry.getValue());
            orderResponse.getLaptopDTOS().add(laptopDTO);
        }
        return orderResponse;
    }
}
