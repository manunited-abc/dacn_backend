package nlu.dacn.dacn_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String name;
    //    private Collection<? extends GrantedAuthority> roles;
    private List<String> roles;

    public JwtResponse(String token, String name, List<String> roles) {
        this.token = token;
        this.name = name;
        this.roles = roles;
    }
}
