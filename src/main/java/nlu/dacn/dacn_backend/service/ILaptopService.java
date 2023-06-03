package nlu.dacn.dacn_backend.service;

import nlu.dacn.dacn_backend.entity.Laptop;
import nlu.dacn.dacn_backend.model.request.LaptopFilter;
import org.springframework.data.domain.Page;

public interface ILaptopService {

    Page<Laptop> getAllLaptop(LaptopFilter filter, int start, int limit);
}
