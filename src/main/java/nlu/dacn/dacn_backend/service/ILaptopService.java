package nlu.dacn.dacn_backend.service;

import nlu.dacn.dacn_backend.dto.request.LaptopDTO;
import nlu.dacn.dacn_backend.dto.response.ImageModel;
import nlu.dacn.dacn_backend.entity.Laptop;
import nlu.dacn.dacn_backend.model.request.LaptopFilter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ILaptopService {

    Page<Laptop> getAllLaptop(LaptopFilter filter, int start, int limit);

    LaptopDTO findLaptopById(Long id);
    List<ImageModel> getImageLinks(Long id);

    List<String> getAllBrand();

    List<String> getAllType();

    List<String> getAllChipCpu();
    List<LaptopDTO> getLaptopByProductName(String key);
}
