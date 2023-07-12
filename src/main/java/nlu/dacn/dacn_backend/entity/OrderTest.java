package nlu.dacn.dacn_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import nlu.dacn.dacn_backend.enumv1.OrderStatus;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


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
    private List<LaptopQuantityEntry> orderLaptops = new ArrayList<>();

    public double calculateTotalPrice() {
        double totalPrice = 0;
        for (LaptopQuantityEntry entry : orderLaptops) {
            Laptop laptop = entry.getLaptop();
            int quantity = entry.getQuantity();
            double price = laptop.getPrice();
            totalPrice += price * quantity;
        }
        return totalPrice;
    }
}
