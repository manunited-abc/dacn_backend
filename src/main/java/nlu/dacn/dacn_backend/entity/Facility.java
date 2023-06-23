//package nlu.dacn.dacn_backend.entity;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.OneToMany;
//import javax.persistence.Table;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table
//@Getter
//@Setter
//public class Facility extends BaseEntity {
//    private String facilityName;
//    private int quantityImport;
//    private int quantityExport;
//    private int quantityInventory;
//    @JsonIgnore
//    @OneToMany(mappedBy = "facility")
//    private List<Laptop> laptops = new ArrayList<>();
//}
