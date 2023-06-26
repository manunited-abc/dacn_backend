package nlu.dacn.dacn_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table
@Getter
@Setter
public class Cart extends BaseEntity {
    private Date exportDate;
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;
    @ElementCollection
    @CollectionTable(name = "cart_laptop",
            joinColumns = @JoinColumn(name = "cart_id"))
    @MapKeyJoinColumn(name = "laptop_id")
    @Column(name = "quantity")
    private Map<Laptop, Integer> cartLaptop = new HashMap<>();

    public void clearItems() {
        cartLaptop.clear();
    }

    public double calculateTotalPrice() {
        double totalPrice = 0;
        for (Map.Entry<Laptop, Integer> entry : cartLaptop.entrySet()) {
            Laptop laptop = entry.getKey();
            int quantity = entry.getValue();
            double price = laptop.getPrice();
            totalPrice += price * quantity;
        }
        return totalPrice;
    }
}
