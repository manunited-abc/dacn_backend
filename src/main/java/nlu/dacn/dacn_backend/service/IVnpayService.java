package nlu.dacn.dacn_backend.service;

import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public interface IVnpayService {
    String generateSanboxLink(String token,double price,Long orderId)throws UnsupportedEncodingException;

    String updateVnpayment(String token, String txnRef, String amount, String responseCode);
}
