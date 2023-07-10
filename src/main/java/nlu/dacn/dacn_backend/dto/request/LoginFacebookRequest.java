package nlu.dacn.dacn_backend.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginFacebookRequest {
    private String fullName;
    private String email;
}
