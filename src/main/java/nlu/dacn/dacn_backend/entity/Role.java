package nlu.dacn.dacn_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Role extends BaseEntity {
    @Column
    private String code;
    @Column
    private String nameCode;
    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private List<Account> accounts = new ArrayList<>();
}
