package nlu.dacn.dacn_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PartyRequest {
    private String name;
    private String type;
    private String status;
}
