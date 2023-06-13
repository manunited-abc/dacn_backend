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
//    @JsonIgnore
//    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
//    private List<CartLaptop> cartLaptops;

//    @JsonIgnore
//    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinTable(
//            name = "cart_laptop",
//            joinColumns = @JoinColumn(name = "cart_id"),
//            inverseJoinColumns = @JoinColumn(name = "laptop_id")
//    )
//    private List<Laptop> laptops = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "cart_laptop",
            joinColumns = @JoinColumn(name = "cart_id"))
    @MapKeyJoinColumn(name = "laptop_id")
    @Column(name = "quantity")
    private Map<Laptop, Integer> cartLaptop = new HashMap<>();
}
