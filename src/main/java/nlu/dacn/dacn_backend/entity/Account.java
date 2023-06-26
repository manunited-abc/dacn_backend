package nlu.dacn.dacn_backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nlu.dacn.dacn_backend.enumv1.State;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
//@Table(uniqueConstraints = {
//        @UniqueConstraint(columnNames = {"user_name"}),
//        @UniqueConstraint(columnNames = {"email"})
//})
@Table
@Getter
@Setter
@ToString
public class Account extends BaseEntity {
    @NotBlank
    @Column
    private String userName;
    @NotBlank
    @Column
    private String password;
    @Column
    private String fullName;
    @Column
    private String phone;
    private Date dob;
    @Column
    @NotBlank
    @Email
    @Size(max = 50)
    private String email;
    @Column
    private String sex;
    @Column
    private String address;
    @Column
    private String addressDetail;
    @Column
    private State state;
    @Column
    private String resetToken;
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "account_role",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();
    @JsonIgnore
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Cart cart;
    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderTest> orderTests;
}
