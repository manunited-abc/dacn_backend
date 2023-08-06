package nlu.dacn.dacn_backend.service;

import nlu.dacn.dacn_backend.dto.response.OrderResponse;
import nlu.dacn.dacn_backend.enumv1.OrderStatus;
import nlu.dacn.dacn_backend.enumv1.PaymentMethod;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public interface IVnpayService {
    OrderResponse orderLaptop(String token);
    String generateSanboxLink(String token,double price,Long orderId)throws UnsupportedEncodingException;

    String updateVnpayment(String token, String txnRef, String amount, String responseCode);
}
