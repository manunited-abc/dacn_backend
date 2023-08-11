package nlu.dacn.dacn_backend.service.impl;

import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.converter.LaptopConverter;
import nlu.dacn.dacn_backend.dto.request.LaptopDTO;
import nlu.dacn.dacn_backend.dto.response.OrderResponse;
import nlu.dacn.dacn_backend.entity.*;
import nlu.dacn.dacn_backend.enumv1.OrderStatus;
import nlu.dacn.dacn_backend.enumv1.PaymentMethod;
import nlu.dacn.dacn_backend.exception.ServiceException;
import nlu.dacn.dacn_backend.repository.AccountRepository;
import nlu.dacn.dacn_backend.repository.OrderRepository;
import nlu.dacn.dacn_backend.repository.OrderTestRepository;
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
    private final LaptopService laptopService;

    private final OrderTestRepository orderTestRepository;


    @Override
    public OrderResponse orderLaptop(String token) {
        Account account = getAccountFromToken(token);
        Cart cart = account.getCart();
        if (cart.getCartLaptop().isEmpty()) {
            throw new ServiceException("Giỏ hàng trống, vui lòng thêm đơn hàng trước khi đặt");
        }

        OrderTest order = new OrderTest();
        order.setAccount(account);
        order.setOrderLaptops(new ArrayList<>(cart.getCartLaptop()));
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentMethod(PaymentMethod.COD);

        //Update quantity of laptop
        for(LaptopQuantityEntry laptopQuantityEntry: cart.getCartLaptop()){
            int oldQuantity = laptopService.getQuantity(laptopQuantityEntry.getLaptop().getId());
            if(oldQuantity<laptopQuantityEntry.getQuantity()){
                throw new ServiceException("Số lượng vượt quá số lượng sản phẩm hiện có");
            }
            laptopService.updateQuantity(laptopQuantityEntry.getLaptop().getId(),oldQuantity - laptopQuantityEntry.getQuantity());
        }
        cart.clearItems();
        account.getOrderTests().add(order);
        accountRepository.save(account);

        List<OrderTest> orderTestList = account.getOrderTests();
        order = orderTestList.get(orderTestList.size() -1);
        OrderResponse ores = toOrderResponse(order);

        return ores;
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

        //Nếu đơn hàng đang được chờ thanh toán vnpay thì bỏ qua
//        for (OrderTest order : orders) {
//            if (order.getOrderStatus() == OrderStatus.PAYWAITING) {
//                orders.remove(order);
//            }
//        }
        for (int i = orders.size() -1 ; i >= 0; i--) {
            OrderTest order = orders.get(i);
            if (order.getOrderStatus() == OrderStatus.PAYWAITING) {
                orders.remove(order);
            }
        }

        OrderResponse orderResponse;
        for (OrderTest order : orders) {
            orderResponse = toOrderResponse(order);
            orderResponseList.add(orderResponse);
            if(isUpdateOrderStatus(order)) {
                orderResponse.setUpdated(true);
            }
        }

        Collections.reverse(orderResponseList);
        return orderResponseList;
    }

    @Override
    public List<OrderResponse> getAllOrderForAdmin(String token) {
        Account account = getAccountFromToken(token);
        if (account.getOrderTests().isEmpty()) {
            throw new ServiceException("Tài khoản của bạn chưa có đơn hàng nào");
        }
        List<OrderResponse> orderResponseList = new ArrayList<>();

        List<OrderTest> orders = orderTestRepository.findAll();

        //Nếu đơn hàng đang được chờ thanh toán vnpay thì bỏ qua
//        for (OrderTest order : orders) {
//            if (order.getOrderStatus() == OrderStatus.PAYWAITING) {
//                orders.remove(order);
//            }
//        }
        for (int i = orders.size() -1 ; i >= 0; i--) {
            OrderTest order = orders.get(i);
            if (order.getOrderStatus() == OrderStatus.PAYWAITING) {
                orders.remove(order);
            }
        }

        OrderResponse orderResponse;
        for (OrderTest order : orders) {
            orderResponse = toOrderResponse(order);
            orderResponseList.add(orderResponse);
            if(isUpdateOrderStatus(order)) {
                orderResponse.setUpdated(true);
            }
        }

        Collections.reverse(orderResponseList);
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
                if (order.getOrderStatus() == OrderStatus.RETURNED) {
                    throw new ServiceException("Đơn hàng đã hoàn trả, không thể cập nhật");
                }
                order.setOrderStatus(orderStatus);
                orderResponse = toOrderResponse(order);
                orderResponse.setUpdated(true);
            }
        }
        accountRepository.save(account);
        return orderResponse;
    }

    public Account getAccountFromToken(String token) {
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

    public OrderResponse toOrderResponse(OrderTest order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setOrderDate(order.getCreatedDate());
        orderResponse.setStatus(order.getOrderStatus().getDescription());
        orderResponse.setTotalPayment(order.calculateTotalPrice());
        orderResponse.setAddressDetail(order.getAccount().getAddressDetail());

        LaptopDTO laptopDTO;
        for(LaptopQuantityEntry entry: order.getOrderLaptops()) {
            laptopDTO = laptopConverter.toLaptopDTO(entry.getLaptop());
            laptopDTO.setQuantity(entry.getQuantity());
            orderResponse.getLaptopDTOS().add(laptopDTO);
        }
        return orderResponse;
    }

    private boolean isUpdateOrderStatus(OrderTest order) {
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            return false;
        }
        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            return false;
        }
        if (order.getOrderStatus() == OrderStatus.RETURNED) {
            return false;
        }
        return true;
    }
}
