package nlu.dacn.dacn_backend.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nlu.dacn.dacn_backend.enumv1.OrderStatus;

@Getter
@Setter
@NoArgsConstructor
public class OrderDTO {
    private String token;
    private Long orderId;
    private OrderStatus orderStatus;
}
