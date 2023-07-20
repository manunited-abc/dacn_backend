package nlu.dacn.dacn_backend.enumv1;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {
    PAYWAITING("Đang chờ thanh toán"),
    PENDING("Đang chờ"),
    PROCESSING("Đang xử lý"),
    SHIPPED("Đã gửi hàng"),
    DELIVERED("Đã giao hàng"),
    RETURNED("Đã trả hàng"),
    CANCELLED("Đã hủy");

    private final String description;
}