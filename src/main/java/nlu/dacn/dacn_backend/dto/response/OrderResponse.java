package nlu.dacn.dacn_backend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nlu.dacn.dacn_backend.dto.request.LaptopDTO;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private String status;
    private Date orderDate;
    private List<LaptopDTO> laptopDTOS = new ArrayList<>();
    private double totalPayment = 0;
    private String addressDetail;
}
