package nlu.dacn.dacn_backend.config;

import lombok.AllArgsConstructor;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@AllArgsConstructor
public class PaymentConfig {
    public static final String IPDEFAULT = "127.0.0.1";
    public static final String VERSIONVNPAY = "2.1.0";
    public static final String COMMAND ="pay";
    public static final String ORDERTYPE = "140000";
    public static final String TMNCODE = "E9IICQS6";
    public static final String CURRCODE = "VND";
    public static final String LOCALDEFAULT = "vn";
    public static final String RETURNURL = "http://localhost:3000/detailPayment/"; //"http://localhost:8085/order/vnpay/return";
    public static final String CHECKSUM = "QYLSQGPNGTPPQIKEXLAJJKXRPKQLXGGV";
    public static final String VNPAYURL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String ORDERINFO = "Thanh toán đơn hàng";


    public static String getRandomNumber(int length) {
        StringBuilder randomBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            randomBuilder.append(random.nextInt(10));
        }
        return randomBuilder.toString();
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public static String hmacSha512(String key, String data) {
        try {
            byte[] hmacBytes = null;

            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA512"));
            hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return DatatypeConverter.printHexBinary(hmacBytes).toLowerCase(); //return plain text data
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }


}
