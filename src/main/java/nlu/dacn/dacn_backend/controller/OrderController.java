package nlu.dacn.dacn_backend.controller;

import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.dto.request.OrderDTO;
import nlu.dacn.dacn_backend.dto.response.OrderResponse;
import nlu.dacn.dacn_backend.dto.response.ResponMessenger;
import nlu.dacn.dacn_backend.enumv1.OrderStatus;
import nlu.dacn.dacn_backend.exception.ServiceException;
import nlu.dacn.dacn_backend.service.IVnpayService;
import nlu.dacn.dacn_backend.service.impl.OrderService;
import nlu.dacn.dacn_backend.utils.TokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    private final IVnpayService vnpayService;

    @PostMapping("/order")
    public ResponseEntity<?> addLaptopToCart(@RequestHeader("Authorization") String authHeader) {
        String token = TokenUtils.getTokenFromHeader(authHeader);
        orderService.orderLaptop(token, OrderStatus.PENDING);
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

    @PostMapping("/order/vnpay")
    public ResponseEntity<?> vnpayOrder(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = TokenUtils.getTokenFromHeader(authHeader);
            OrderResponse orderResponse = orderService.orderLaptop(token, OrderStatus.PAYWAITING);
            String urlpayment = vnpayService.generateSanboxLink(token, orderResponse.getTotalPayment(), orderResponse.getId());
            return new ResponseEntity<String>(urlpayment, HttpStatus.OK);
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("Mã hoá không được hỗ trợ");
        }
    }

    @PutMapping("/order/vnpay/return")
    public ResponseEntity<?> upatevnpayment(
            @RequestParam(value = "token", required = false) String token,
            Principal principal,
            @RequestParam(value = "vnp_Amount", required = false) String amount,
            @RequestParam(value = "vnp_BankCode", required = false) String bankCode,
            @RequestParam(value = "vnp_BankTranNo", required = false) String bankTranNo,
            @RequestParam(value = "vnp_CardType", required = false) String cardType,
            @RequestParam(value = "vnp_OrderInfo", required = false) String orderInfo,
            @RequestParam(value = "vnp_PayDate", required = false) String payDate,
            @RequestParam(value = "vnp_ResponseCode", required = false) String responseCode,
            @RequestParam(value = "vnp_TmnCode", required = false) String tmnCode,
            @RequestParam(value = "vnp_TransactionNo", required = false) String transactionNo,
            @RequestParam(value = "vnp_TxnRef", required = false) String txnRef,
            @RequestParam(value = "vnp_SecureHashType", required = false) String secureHashType,
            @RequestParam(value = "vnp_SecureHash", required = false) String secureHash
    ) {
        String updatemessage = vnpayService.updateVnpayment(token,txnRef,amount,responseCode);

        return new ResponseEntity<String>(updatemessage,HttpStatus.OK);
    }

}
