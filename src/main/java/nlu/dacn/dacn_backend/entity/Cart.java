package nlu.dacn.dacn_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

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
    @CollectionTable(name = "cart_laptop", joinColumns = @JoinColumn(name = "cart_id"))
    List<LaptopQuantityEntry> cartLaptop = new ArrayList<>();

    public void clearItems() {
        cartLaptop.clear();
    }
}
