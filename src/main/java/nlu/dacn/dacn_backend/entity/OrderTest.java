package nlu.dacn.dacn_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import nlu.dacn.dacn_backend.enumv1.OrderStatus;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table
@Getter
@Setter
public class OrderTest extends BaseEntity {
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private OrderStatus orderStatus;

    @ElementCollection
    @CollectionTable(name = "order_laptop", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyJoinColumn(name = "laptop_id")
    @Column(name = "quantity")
    private Map<Laptop, Integer> orderedLaptops = new HashMap<>();

    public double calculateTotalPrice() {
        double totalPrice = 0;
        for (Map.Entry<Laptop, Integer> entry : orderedLaptops.entrySet()) {
            Laptop laptop = entry.getKey();
            int quantity = entry.getValue();
            double price = laptop.getPrice();
            totalPrice += price * quantity;
        }
        return totalPrice;
    }
}
