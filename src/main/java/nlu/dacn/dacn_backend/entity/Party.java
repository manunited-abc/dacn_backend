package nlu.dacn.dacn_backend.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
@Getter
@Setter
public class Party extends BaseEntity {
    private String name;
    private String type;
    private String status;
}
