package nlu.dacn.dacn_backend.entity;

import lombok.Getter;
import lombok.Setter;
import nlu.dacn.dacn_backend.enumv1.LaptopState;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
@Getter
@Setter
public class Laptop extends BaseEntity {
    private String productName;
    private String brand;
    private double price;
    private String cpu;
    private String chipCpu;
    private String ram;
    private String storage;
    private String display;
    private String graphics;
    private String color;
    private String battery;
    private String weight;
    private String type;
    private LaptopState laptopState;
    private int quantity;
    private String linkAvatar;
}
