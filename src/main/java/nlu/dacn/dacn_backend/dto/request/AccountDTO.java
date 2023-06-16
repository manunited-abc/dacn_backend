package nlu.dacn.dacn_backend.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nlu.dacn.dacn_backend.entity.Role;
import nlu.dacn.dacn_backend.enumv1.State;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class AccountDTO extends AbstractDTO<AccountDTO>{
    private String userName;
    private String password;
    private String fullName;
    private String phone;
    private Date dob;
    private String email;
    private String sex;
    private String address;
    private String addressDetail;
    private Integer status;
    private State state;
    private List<Role> roles;
}