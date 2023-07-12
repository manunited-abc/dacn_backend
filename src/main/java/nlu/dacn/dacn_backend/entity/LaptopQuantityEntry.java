package nlu.dacn.dacn_backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
@Getter
@Setter
public class LaptopQuantityEntry {
    @ManyToOne
    @JoinColumn(name = "laptop_id")
    private Laptop laptop;
    private int quantity;
}
