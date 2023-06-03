package nlu.dacn.dacn_backend.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LaptopFilter {
    private List<String> types = new ArrayList<>();
    private List<String> brands = new ArrayList<>();
    private List<String> chipCpus= new ArrayList<>();
}