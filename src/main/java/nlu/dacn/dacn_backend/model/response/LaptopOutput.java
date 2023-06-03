package nlu.dacn.dacn_backend.model.response;

import lombok.Getter;
import lombok.Setter;
import nlu.dacn.dacn_backend.entity.Laptop;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LaptopOutput {
    private int page;
    private int totalPage;
    private List<Laptop> laptopList = new ArrayList<>();
}