package nlu.dacn.dacn_backend.enumv1;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentMethod {
    VNPAY("VNPAY"),

    COD("COD");

    private final String payment;
}
