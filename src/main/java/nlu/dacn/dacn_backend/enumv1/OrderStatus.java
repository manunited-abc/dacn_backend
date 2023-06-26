package nlu.dacn.dacn_backend.enumv1;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {
    PENDING("Đang chờ"),
    PROCESSING("Đang xử lý"),
    SHIPPED("Đã gửi hàng"),
    DELIVERED("Đã giao hàng"),
    CANCELLED("Đã hủy");

    private final String description;
}