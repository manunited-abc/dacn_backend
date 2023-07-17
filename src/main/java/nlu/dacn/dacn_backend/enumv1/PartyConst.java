package nlu.dacn.dacn_backend.enumv1;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class PartyConst{
    @AllArgsConstructor
    public enum PartyType{
        CHIP_CPU("CPU"),
        CPU("Vi xử lý (CPU)"),
        BRAND("Hãng"),
        DISPLAY("Màn hình"),
        GRAPHICS("Card đồ họa (GPU)"),
        RAM("Ram"),
        STORAGE("Lưu trữ"),
        TYPE("Loại");
        @Getter
        public String description;
    }
    @AllArgsConstructor
    public enum PartyStatus{
        ACTIVE("Hoạt động"),
        INACTIVE("Vô hiệu hoá");
        @Getter
        public String description;
    }
}
