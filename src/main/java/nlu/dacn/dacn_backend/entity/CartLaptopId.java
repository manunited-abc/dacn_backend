package nlu.dacn.dacn_backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class CartLaptopId implements Serializable {
    private Long cartId;
    private Long laptopId;
}
