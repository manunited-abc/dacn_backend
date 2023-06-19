package nlu.dacn.dacn_backend.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageModel {
    private Long uid;
    private String name;
    private String url;
}