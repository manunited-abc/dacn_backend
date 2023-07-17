package nlu.dacn.dacn_backend.service.impl;

import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.config.PaymentConfig;
import nlu.dacn.dacn_backend.converter.LaptopConverter;
import nlu.dacn.dacn_backend.entity.Account;
import nlu.dacn.dacn_backend.entity.OrderTest;
import nlu.dacn.dacn_backend.enumv1.OrderStatus;
import nlu.dacn.dacn_backend.exception.ServiceException;
import nlu.dacn.dacn_backend.repository.AccountRepository;
import nlu.dacn.dacn_backend.repository.OrderRepository;
import nlu.dacn.dacn_backend.security.jwt.JwtTokenProvider;
import nlu.dacn.dacn_backend.service.IVnpayService;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@RequiredArgsConstructor
public class VnPayService implements IVnpayService {
    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;
    private final LaptopConverter laptopConverter;
    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;

    @Override
    public String generateSanboxLink(String token,double price,Long orderId) throws UnsupportedEncodingException {
        long amount = (long)price * 100;
//
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", PaymentConfig.VERSIONVNPAY);
        vnp_Params.put("vnp_Command", PaymentConfig.COMMAND);
        vnp_Params.put("vnp_TmnCode", PaymentConfig.TMNCODE);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", PaymentConfig.CURRCODE);
//        vnp_Params.put("vnp_BankCode", requestParams.getBankCode());
        vnp_Params.put("vnp_TxnRef", String.valueOf(orderId));
        vnp_Params.put("vnp_OrderInfo", PaymentConfig.ORDERINFO);
        vnp_Params.put("vnp_OrderType", PaymentConfig.ORDERTYPE);
        vnp_Params.put("vnp_Locale", PaymentConfig.LOCALDEFAULT);
        vnp_Params.put("vnp_ReturnUrl", PaymentConfig.RETURNURL + "?token=" + token);
        vnp_Params.put("vnp_IpAddr", PaymentConfig.IPDEFAULT);

//        Date dt = new Date();
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//        String dateString = format.format(dt);
//
//        String vnp_CreateDate = dateString;
//        vnp_Params.put("vnp_CreateDate",vnp_CreateDate);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        //Add Params of 2.1.0 Version
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {

                //build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.CHECKSUM, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = PaymentConfig.VNPAYURL + "?" + queryUrl;

        return paymentUrl;
    }

    @Override
    public String updateVnpayment(String token, String txnRef, String amount, String responseCode) {
        String username = jwtTokenProvider.getUserNameFromToken(token);
        Optional<Account> optionalAccount = accountRepository.findByUserName(username);
        if (optionalAccount.isEmpty()) {
            throw new ServiceException("Không tìm thấy tài khoản");
        }
        Account account = optionalAccount.get();

        Optional<OrderTest> orderTestOptional = orderRepository.findById(Long.valueOf(txnRef));
        if (orderTestOptional.isEmpty()){
            throw new ServiceException("Đơn hàng không tồn tại");
        }
        OrderTest orderTest = orderTestOptional.get();

        if (!responseCode.equalsIgnoreCase("00") || orderTest.calculateTotalPrice() != (Long.parseLong(amount)/100)){
            return "Giao dịch mã đơn " + txnRef + " đã được huỷ";
        }

        orderService.updateOrderStatus(token, Long.valueOf(txnRef), OrderStatus.PENDING);

        return "Giao dịch mã đơn " + txnRef + " đã thanh toán thành công";
    }
}
