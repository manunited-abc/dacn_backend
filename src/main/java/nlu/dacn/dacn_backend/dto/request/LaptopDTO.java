package nlu.dacn.dacn_backend.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nlu.dacn.dacn_backend.enumv1.LaptopState;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class LaptopDTO extends AbstractDTO<LaptopDTO> {
    @NotBlank(message = "Nhà kho không được để trống")
    private Long facilityId;
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String productName;
    @NotBlank(message = "Hãng sản xuất không được để trống")
    private String brand;
    @NotBlank(message = "Giá tiền không được để trống")
    private double price;
    private double totalAmout;
    private String cpu;
    @NotBlank(message = "Loại chip CPU không được để trống")
    private String chipCpu;
    private String ram;
    private String storage;
    private String display;
    private String graphics;
    private String color;
    private String battery;
    private String weight;
//    private LaptopState laptopState;
    private List<String> listImages = new ArrayList<>();
    @Min(value = 1, message = "Số lượng nhập phải lớn hơn 0")
    private int quantity;
    @NotBlank(message = "Loại laptop không được để trống")
    private String type;
    @NotNull
    private String linkAvatar;
    private String urlSeo;
}
